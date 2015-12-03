To find duplicates of a record Duke cannot compare that record with all other records, because record comparisons are slow, and this would have O(n<sup>2</sup>) performance. Instead, Duke uses the Database interface, where all records are indexed. When looking for duplicates of a record, Duke calls `findCandidateMatches(record)` on the database, and gets back a list of potential matches, which are then compared in detail. How the candidates are found depends on the specific Database implementation used.

By default Duke uses an in-memory LuceneDatabase (see below), but you can override this and configure the database yourself by including the following in the [XMLConfig](XMLConfig.md):

```
  <database class="no.priv.garshol.duke.databases.WhateverDatabase">
    <param name="..." value="..."/>
    ...
  </database>
```

You can also write your own implementation of the Database interface and configure it using the syntax above.

# LuceneDatabase #

This is the original database, and stll the default. It indexes up the records in a Lucene index and does full-text queries to find candidates. By default it does fuzzy searching, which is slow. It also supports geospatial searching.

Supports the following parameters:
  * **max-search-hits**: The maximum number of candidate records to return. By default there is no limitation. Setting this to 10, for example, can speed up processing dramatically.
  * **min-relevance**: Don't return candidates which have lower Lucene relevance ranking than this. Relevance is always a number between 1.0 and 0.0, and the most relevant record always has 1.0. 0.9 is often a good value. Defaults to 0.0.
  * **fuzzy-search**: Whether to use fuzzy searching, which is slow. Defaults to true.
  * **path**: Where to store the Lucene index to disk. If not set, the index is kept in memory.

# InMemoryDatabase #

This is a naive implementation which always returns all records as candidates. Has the benefit that it never loses matches, and for small datasets it can be very fast. For big datasets it is fantastically slow, as it has O(n<sup>2</sup>) performance. Has no tunable parameters.

# KeyValueDatabase #

This database is essentially based on full-text search like the LuceneDatabase, but is faster, since it only supports exactly what Duke needs and can avoid some overhead. The downsides of this database is that it is only in-memory, it doesn't support fuzzy and geospatial searching, and it has poorer relevance ranking.

Supports the following parameters:
  * **max-search-hits**: The maximum number of candidate records to return. By default there is no limitation. Setting this to 10, for example, can speed up processing dramatically.
  * **min-relevance**: Don't return candidates which have lower Lucene relevance ranking than this. Relevance is always a number between 1.0 and 0.0, and the most relevant record always has 1.0. 0.1 is often a good value. Defaults to 0.0.

# InMemoryBlockingDatabase #

This is by far the fastest backend, but it has the disadvantage that it requires the user to implement a KeyFunction, which has to be tuned to the actual data. It works by computing a key for each record, and then indexing the records by key. It will return as candidates all records with the same key as the input record, and a configurable number on either side (the index is sorted by key).

Performance is highly sensitive to the number of records per key, and recall is also very sensitive to how records cluster. With good KeyFunctions it can be very accurate and blazingly fast. Examples of key functions for person records with addresses can be: first token in NAME longer than 3 characters followed by zip code. Or zip code plus first token in ADDRESS longer than 3 characters. It's recommended to use at least two KeyFunction impementations for better recall.

In order to help you tune your KeyFunctions the no.priv.garshol.duke.DatabaseStatistics command-line tool can be used to show how many keys have how many different records mapping to them.

The database supports the following parameters:
  * **window-size**: Sets the minimum number of records to collect from keys higher and lower than the key of the current record. If set to zero only records with the same key will be compared. Default: 5.
  * **key-functions**: Sets the key functions used for blocking. **REQUIRED**

# MapDBBlockingDatabase #

A database that works exactly like the other blocking database, except that it keeps the key blocks in a [MapDB](http://www.mapdb.org/) database, either in memory or on disk. Since MapDB uses off-heap memory this database may perform better in-memory than the one above for very big datasets. Performance on disk is also quite good, so this is the preferred database for very lage datasets.

The database supports the following parameters:
  * **window-size**: Sets the minimum number of records to collect from keys higher and lower than the key of the current record. If set to zero only records with the same key will be compared. Default: 5.
  * **key-functions**: Sets the key functions used for blocking. **REQUIRED**
  * **cache-size**: The size of the MapDB instance cache. Default: 32,768.
  * **file**: The file to store the database in. If not specified the database is kept in memory.
  * **async**: Whether to write to disk asynchronously. Default: true.
  * **mmap**: Whether to use memory-mapped files. Default: true.
  * **compression**: Whether to compress the database. Default: false.
  * **snapshot**: Whether to use snapshots, which avoids some locking. Default: false.
  * **notxn**: Turns off transaction journal for better performance. Default: false.