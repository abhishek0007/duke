# Introduction #

This page contains a list of tips for making Duke run faster.

# Turn off fuzzy searching #

Duke uses fuzzy searching in Lucene by default, which is very slow, but has better recall. You can configure Lucene to turn this off. See DatabaseConfig for details.

# Reducing record comparisons #

By default, for each record Duke will compare it with all candidate records returned by Lucene. As a rule only a small fraction of these are actually considered matches. In order to reduce the number of candidates compared you can set some tuning parameters. Using these can improve performance with orders of magnitude, at the cost of losing some percentage points of recall. See DatabaseConfig for details.

# Bigger batches #

For each batch of records, Duke will index the entire batch, then commit and reopen the IndexReader. This causes Lucene to lose its cache, slowing down searches. To reduce the impact, you can increase your batch sizes. The only downside to a bigger batch size is that it requires more memory, as the entire batches are held in memory. The default batch size is 40,000.

Set batch size with command-line parameters if running Duke on the command-line, or with arguments to the `deduplicate()` and `link()` methods if using the API.

# More memory #

Giving the Java process more memory will enable Lucene to cache more of the search index, speeding up searching. Note that you must take care not to use so much memory that the machine starts swapping, because if you do everything will be dog slow.

# Multithreading #

This one is obvious: use more than one thread. Do not use more threads than you have CPUs, however, or you'll find that the overhead of managing the threads eats up the gain.

You can control the number of threads with `--threads=N` on the command-line, or `Processor.setThreads(N)` in the API.

# Use a faster database #

The Lucene database is not the fastest option in Duke. The KeyValueDatabase and the blocking databases are considerably faster. See DatabaseConfig for more details.

# In record linkage mode #

When using record linkage mode, put the biggest group first in the configuration. This makes linking run faster. Also, when tuning the configuration you can run the command-line tool with `--noreindex`, which is much faster, as it will only process the second group.

# If it's still slow #

If performance is still low, try running on a subset of the data that completes in a reasonable amount of time (but is still slow!), and pass the `--profile` argument to Duke. This will make Duke output performance statistics as it runs. These may give you more of an idea of what to tune. If they're not helpful, try contacting the [mailing list](http://groups.google.com/group/duke-dedup).

Below is an example of `--profile` output:

```
Duke version 1.1-SNAPSHOT, build 2,132 (2013-07-24), built by larsga
LuceneDatabase, max-search-hits: 10, min-relevance: 0.9
  org.apache.lucene.store.RAMDirectory@7c4c51 lockFactory=org.apache.lucene.store.SingleInstanceLockFactory@7a148bd3
Threads: 1
40000 processed, 1810 records/second; comparisons: 203755
80000 processed, 1751 records/second; comparisons: 409683
120000 processed, 1535 records/second; comparisons: 613637
160000 processed, 1234 records/second; comparisons: 857273
200000 processed, 772 records/second; comparisons: 1167427
240000 processed, 546 records/second; comparisons: 1483187
280000 processed, 579 records/second; comparisons: 1790777
320000 processed, 577 records/second; comparisons: 2071269
320982 processed, 513 records/second; comparisons: 2077252
Run completed, 837 records/second
320982 records total in 383 seconds
Reading from source: 9 (2%)
Indexing: 12 (3%)
Searching: 330 (87%)
Comparing: 25 (6%)
Callbacks: 0 (0%)
```

Note that if you are using Duke via the API you can still do profiling, by calling `Processor.setPerformanceProfiling(true)`.