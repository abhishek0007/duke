# Introduction #

Working on this project I've found that what moves Duke forward is having concrete use cases and feature requests from users. Without a real use case to try new features out in it's really difficult to progress. So I've abandoned the idea of an explicit road map.

If there's something you want from Duke that's not there, please let me know, via the [issue tracker](http://code.google.com/p/duke/issues/list), the [mailing list](https://groups.google.com/forum/#!forum/duke-dedup), or by email to me.

# Interesting features #

At the moment, I think the biggest problem is to create configurations. There are two ways we can make that easier:

  * Create a graphical user interface for building configurations. I don't enjoy building such things myself, so contributions would be welcome.
  * Add support for generating configurations automatically. Porting the GeneticAlgorithm to Java would be a good start. (See [issue 119](https://code.google.com/p/duke/issues/detail?id=119).)

Some things Duke could use:

  * More comparators. Both better string comparators, and comparators for other data types, such as numbers, dates, etc.

  * More data sources. There ought to be one for XML, for example. The difficulty is adding these without pulling in millions of dependencies.

  * More work on the link database and equivalence class database concepts, incuding better documentation. Basically, we need to make it easier to work with links once Duke has discovered them. Here we need more understanding of people's needs. See [this discussion](https://groups.google.com/forum/?fromgroups#!topic/duke-dedup/XtvO2djmBYE), and please contribute if you can.

In the longer term it would be great to have:

  * MapReduce support, probably using Hadoop, for handling really big data sets.

Another thing that would be interesting would be to see whether Duke works for Chinese/Japanese data, or whether we need to tune the comparators to handle that type of data. However, in order to be able to work meaningfully on this we need some test data.