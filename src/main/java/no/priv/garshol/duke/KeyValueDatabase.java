
package no.priv.garshol.duke;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import no.priv.garshol.duke.utils.StringUtils;

/**
 * A database that uses a key-value store to index and find records.
 * Currently an experimental proof of concept to see if this approach
 * really can be faster than Lucene.
 */
public class KeyValueDatabase implements Database {
  private Configuration config;
  private KeyValueStore store;
  private int max_search_hits;
  private float min_relevance;
  private static final boolean DEBUG = false;
  
  public KeyValueDatabase(Configuration config,
                          DatabaseProperties dbprops) {
    this.config = config;
    this.max_search_hits = dbprops.getMaxSearchHits();
    this.min_relevance = dbprops.getMinRelevance();
    this.store = new InMemoryKeyValueStore();
  }

  /**
   * Returns true iff the database is held entirely in memory, and
   * thus is not persistent.
   */
  public boolean isInMemory() {
    return store.isInMemory();
  }

  /**
   * Add the record to the index.
   */
  public void index(Record record) {
    // FIXME: check if record is already indexed

    // allocate an ID for this record
    long id = store.makeNewRecordId();
    store.registerRecord(id, record);
    
    // go through ID properties and register them
    for (Property p : config.getIdentityProperties()) {
      Collection<String> values = record.getValues(p.getName());
      if (values == null)
        continue;
      
      for (String extid : values)
        store.registerId(id, extid);
    }

    // go through lookup properties and register those
    for (Property p : config.getLookupProperties()) {
      String propname = p.getName();
      Collection<String> values = record.getValues(propname);
      if (values == null)
        continue;

      for (String value : values) {
        String[] tokens = StringUtils.split(value);
        for (int ix = 0; ix < tokens.length; ix++)
          store.registerToken(id, propname, tokens[ix]);
      }
    }
  }

  /**
   * Look up record by identity.
   */
  public Record findRecordById(String id) {
    return store.findRecordById(id);
  }

  /**
   * Look up potentially matching records.
   */
  public Collection<Record> findCandidateMatches(Record record) {
    if (DEBUG)
      System.out.println("---------------------------------------------------------------------------");
    
    // do lookup on all tokens from all lookup properties
    // (we only identify the buckets for now. later we decide how to process
    // them)
    List<Bucket> buckets = new ArrayList();
    for (Property p : config.getLookupProperties()) {
      String propname = p.getName();
      Collection<String> values = record.getValues(propname);
      if (values == null)
        continue;

      for (String value : values) {
        String[] tokens = StringUtils.split(value);
        for (int ix = 0; ix < tokens.length; ix++) {
          Bucket b = store.lookupToken(propname, tokens[ix]);
          long[] ids = b.records;
          if (ids == null)
            continue;
          if (DEBUG)
            System.out.println(propname + ", " + tokens[ix] + ": " + b.nextfree);
          buckets.add(b);
        }
      }
    }
    
    // preprocess the list of buckets
    Collections.sort(buckets);
    double score_sum = 0.0;
    for (Bucket b : buckets)
      score_sum += b.getScore();
      
    double score_so_far = 0.0;
    int threshold = buckets.size() - 1;
    for (; (score_so_far / score_sum) < min_relevance; threshold--) {
      score_so_far += buckets.get(threshold).getScore();
      if (DEBUG)
        System.out.println("score_so_far: " + (score_so_far/score_sum) + " (" +
                           threshold + ")");
    }
    // bucket.get(threshold) made us go over the limit, so we need to step
    // one back
    threshold++;
    if (DEBUG)
      System.out.println("Threshold: " + threshold);
    
    // the collection of candidates
    Map<Long, Score> candidates = new HashMap();

    // go through the buckets that we're going to collect candidates from
    for (int ix = 0; ix < threshold; ix++) {
      Bucket b = buckets.get(ix);
      long[] ids = b.records;
      double score = b.getScore();
      
      for (int ix2 = 0; ix2 < b.nextfree; ix2++) {
        Score s = candidates.get(ids[ix2]);
        if (s == null) {
          s = new Score(ids[ix2]);
          candidates.put(ids[ix2], s);
        }
        s.score += score;
      }
    }

    // there might still be some buckets left below the threshold. for
    // these we go through the existing candidates and check if we can
    // find them in the buckets.
    for (int ix = threshold; ix < buckets.size(); ix++) {
      Bucket b = buckets.get(ix);
      double score = b.getScore();
      for (Score s : candidates.values())
        if (b.contains(s.id))
          s.score += score;
    }

    if (DEBUG)
      System.out.println("candidates: " + candidates.size());
    
    // if the cutoff properties are not set we can stop right here
    // FIXME: it's possible to make this a lot cleaner
    if (max_search_hits > candidates.size() && min_relevance == 0.0) {
      Collection<Record> cands = new ArrayList(candidates.size());
      for (Long id : candidates.keySet())
        cands.add(store.findRecordById(id));
      if (DEBUG)
        System.out.println("final: " + cands.size());
      return cands;
    }
    
    // flatten candidates into an array, prior to sorting etc
    int ix = 0;
    Score[] scores = new Score[candidates.size()];
    double max_score = 0.0;
    for (Score s : candidates.values()) {
      scores[ix++] = s;
      if (s.score > max_score)
        max_score = s.score;
      if (DEBUG && false)
        System.out.println("" + s.id + ": " + s.score);
    }

    // allow map to be GC-ed
    candidates = null;

    // filter candidates with min_relevance and max_search_hits. do
    // this by turning the scores[] array into a priority queue (on
    // .score), then retrieving the best candidates. this shows a big
    // performance improvement over sorting the array
    PriorityQueue pq = new PriorityQueue(scores);
    int count = Math.min(scores.length, max_search_hits);
    Collection<Record> records = new ArrayList(count);
    for (ix = 0; ix < count; ix++) {
      Score s = pq.next();
      if (s.score < min_relevance)
        break; // we're finished
      records.add(store.findRecordById(s.id));
    }

    if (DEBUG)
      System.out.println("final: " + records.size());
    return records;
  }

  /**
   * Flushes all changes to disk. For in-memory databases this is a
   * no-op.
   */
  public void commit() {
    store.commit();
  }
  
  /**
   * Stores state to disk and closes all open resources.
   */
  public void close() {
    store.close();
  }

  public String toString() {
    return "KeyValueDatabase(" + store + ")";
  }

  static class Score implements Comparable<Score> {
    public long id;
    public double score;

    public Score(long id) {
      this.id = id;
    }

    public int compareTo(Score other) {
      if (other.score < score)
        return -1;
      else if (other.score > score)
        return 1;
      else
        return 0;
    }
  }

  static class PriorityQueue {
    private Score[] scores;
    private int size;

    public PriorityQueue(Score[] scores) {
      this.scores = scores;
      this.size = scores.length; // heap is always full to begin with
      build_heap();
    }

    /**
     * Turns the random array into a heap.
     */
    private void build_heap() {
      for (int ix = (size / 2); ix >= 0; ix--)
        heapify(ix);
    }

    /**
     * Assuming binary trees rooted at left(ix) and right(ix) are
     * already heaped, but scores[ix] may not be heaped, rebalance so
     * that scores[ix] winds up in the right place, and subtree rooted
     * at ix is correctly heaped.
     */
    private void heapify(int ix) {
      int left = ix * 2;
      if (left > size - 1)
        return; // ix is a leaf, and there's nothing to be done
      
      int right = left + 1;
      int largest;
      if (scores[left].score > scores[ix].score)
        largest = left;
      else
        largest = ix;

      if (right < size - 1 && scores[right].score > scores[largest].score)
        largest = right;

      if (largest != ix) {
        Score tmp = scores[largest];
        scores[largest] = scores[ix];
        scores[ix] = tmp;
        heapify(largest);
      }
    }

    public Score next() {
      Score next = scores[0];
      size--;
      if (size >= 0) {
        scores[0] = scores[size];
        scores[size] = null;
        heapify(0);
      }
      return next;
    }
  }
}
