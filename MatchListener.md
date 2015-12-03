# Introduction #

While running, Duke will call methods on all the registered [MatchListener](http://www.garshol.priv.no/download/duke/no/priv/garshol/duke/matchers/MatchListener.html)s. This can be used to implement your own code for making use of links found.

Duke comes with built-in listeners for writing results to a text file, an NTriples file or a JDBC database.

# Details #

The first callback is `startProcessing()`, which is called before processing begins.

Then, `batchReady(size)` is called, to indicate that a new batch has been read from the data source(s) and is ready for processing.

For each record there will either be one or more call to `matches(...)` or `matchesPerhaps(...)`, or there will be a single call to `noMatchesFor(record)`.

Once the batch is finished, `batchDone()` is called.

Finally, when there are no more records, `endProcessing()` is called.