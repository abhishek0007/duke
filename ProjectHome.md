# Duke has moved to [Github](https://github.com/larsga/Duke) #

Duke is a fast and flexible deduplication (or entity resolution, or record linkage) engine written in Java on top of Lucene.  The latest version is 1.1 (see ReleaseNotes).

Features

  * High performance.
  * Highly configurable.
  * Support for CSV, JDBC, SPARQL, and NTriples [DataSources](DataSources.md).
  * Many built-in [comparators](Comparator.md).
  * Plug in your own data sources, comparators, and [cleaners](Cleaner.md).
  * GeneticAlgorithm for automatically tuning configurations.
  * Command-line client for getting started.
  * API for embedding into any kind of application.
  * Support for batch processing and continuous processing.
  * Can maintain database of links found via JNDI/JDBC.
  * Can run in multiple threads.

# Duke has moved to [Github](https://github.com/larsga/Duke) #

The GettingStarted page explains how to get started and has links to further documentation.
[This blog post](http://www.garshol.priv.no/blog/217.html) describes the basic approach taken to match records. It does not deal with the Lucene-based lookup, but describes an early, slow O(n^2) prototype. [This early presentation](http://www.slideshare.net/larsga/deduplication) describes the ideas behind the engine and the intended architecture; [a later and more up to date presentation](http://www.slideshare.net/larsga/linking-data-without-common-identifiers) has more practical detail and examples. There's also the ExamplesOfUse page, which lists real examples of using Duke, complete with data and configurations.

Contributions, whether issue reports or patches, are very much welcome. Note that the source code is in Mercurial, so you can clone the repository and publish your changes here.

If you have questions or problems, please register an issue in the issue tracker, or post to the [the mailing list](http://groups.google.com/group/duke-dedup). If you don't want to join the list you can always write to me at `larsga [a] garshol.priv.no`, too.

# Duke has moved to [Github](https://github.com/larsga/Duke) #