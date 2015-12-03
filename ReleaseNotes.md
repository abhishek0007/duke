# Duke 1.2 (coming soon) #

The main change in this version is the new, and much faster, blocking backends. Note that this version introduces API changes and changes to the [XMLConfig](XMLConfig.md) that are not backwards compatible. Read on for details.

Other new features:

  * Added longest common substring comparator.
  * LuceneDatabase now uses fuzzy search by default (which is much slower).
  * New default Record implementation, faster and uses less memory.
  * Support for changing CSV value separator.
  * Databases are now pluggable (see DatabaseConfig).
  * Improved inference of links in LinkDatabase.

API changes:

  * The ModifiableRecord interface was added.
  * Two methods have been added to the Database interface.
  * The DatabaseProperties class has been removed.
  * All Database implementations have moved into the duke.databases package.
  * The Configuration.createDatabase method is replaced by Configuration.getDatabase.
  * The Link and LinkFileWriter interfaces have changed, and now require a confidence value.
  * The TestFileUtils class is deprecated, and will be removed in the next release.

**NOTE:** The change to the XMLConfig concerns how databases are configured. The `<param>` elements inside the root element now have no effect, and the `<path>` element is no longer supported. Instead, the `<database>` element has been introduced. See XMLConfig and DatabaseConfig for details.

**NOTE:** The link database schema has changed: the confidence column has been included. If you have an existing link database you need to add this column.

**NOTE**:  The link file syntax has changed to be a proper CSV file and include the confidence.

# Duke 1.1 #

The main new feature in this version is the GeneticAlgorithm, which can be used to automatically tune a configuration.

Other new features:

  * Support for geosearch.

API changes:

  * Moved Column class to .datasources.
  * Property is now an interface, and no longer a class.
  * Added setDoInference to InMemoryLinkDatabase.
  * Added ConfigWriter, which can export configurations to XML.
  * Added LinkFileWriter.
  * Added Processor.setProfiling and Processor.getProfiler to allow performance profiling via API.
  * Added Processor.removeMatchListener
  * Added Configuration.copy() and Property.copy().

Changes to command-line tools:

  * Added the RecordSearch tool.
  * Added --reindex option to DebugCompare.
  * Added --lookups to .Duke

Bug fixes:

  * SPARQL data source was broken. Now fixed, and test cases added.
  * [Issue 124](https://code.google.com/p/duke/issues/detail?id=124): --interactive fails in Eclipse
  * [Issue 114](https://code.google.com/p/duke/issues/detail?id=114): --noreindex causes matches to disappear

# Duke 1.0 #

This is a major new release of Duke. After 18 months of work it is now feature-complete enough to deserve a 1.0 release.

Performance improvements:

  * Support for multi-threading added
  * Using NIOFSDirectory on all platforms except Windows
  * New in-memory backend, faster than Lucene (experimental)

Changes to [Comparator](Comparator.md)s:

  * Geo-coordinate comparator added.
  * Q-grams comparator added.
  * Levenshtein implementation is now faster
  * Weighted Levenshtein weight estimator now knows position in string ([issue 81](https://code.google.com/p/duke/issues/detail?id=81))

Changes to [Cleaner](Cleaner.md)s:

  * Added PhoneNumberCleaner
  * Extended and generalized regexp cleaner
  * Removed sub-cleaner concept, added support for multiple cleaners

Other improvements:

  * Implemented user control over lookup props
  * Upgraded to Lucene 4.0
  * Added MatchListener.startProcessing() callback
  * Removed some MatchListener callback methods (weren't thread-safe)
  * InMemoryLinkDatabase now complete and tested
  * LinkDatabaseMatchListener bug fixes
  * Better validation of configurations
  * JDBCEquivalenceClassDatabase added
  * RDBMSLinkDatabase performance improvement

Changes to command-line client:

  * Added data debug mode
  * Fixed bug with reusing link file as test file
  * Added pretty-printing of records
  * Better interactive debugging behaviour
  * Improvements to DebugCompare tool
  * Added performance profiling to command-line client

Bugs fixed:

  * [Issue 83](https://code.google.com/p/duke/issues/detail?id=83): Look up record by ID when ID is a URI.
  * [Issue 90](https://code.google.com/p/duke/issues/detail?id=90): Bug in command-line option parser.
  * Bug in CSV data source fixed

# Duke 0.6 #

The following changes have been made in this version:

  * A change to the calculation of property probabilities when values do not match exactly. This means that you may need to adjust the probabilities and thresholds in your applications.
  * Upgraded to Lucene 3.6.1.
  * Improvements to NorwegianCompanyNameCleaner and NorwegianAddressCleaner.

This version adds the following features:

  * A weighted Levenshtein comparator.
  * A Metaphone comparator.
  * A Jaccard index comparator.
  * A prototype of a comparator using a Norwegian version of Metaphone.
  * A generic value cleaner.
  * Support for setting objects as parameters of other objects.

# Duke 0.5 #

This version has seen the internals of the code refactored and cleaned up. In addition, the following has been added:

  * Support for pluggable backends in addition to the Lucene backend
  * A naïve in-memory backend that matches all pairs of records
  * Performance tuning options in the configuration
  * Data sources can now split a single value into several properties
  * New cleaners: RegexpCleaner, MappingFileCleaner
  * The NTriples data source now supports incremental loading
  * Properties can now be declared as type="ignore"
  * Command-line tool has an --interactive option
  * Command-line tool has an --noreindex option
  * Levenshtein comparator has been substantially optimized
  * Several bug fixes
  * Many more tests
  * In-memory link database implementation
  * inferLink() method on link database interface

# Duke 0.4 #

This version of Duke introduces:

  * Added JNDI data source for connecting to databases via JNDI (thanks to FMitzlaff).
  * In-memory data source added (thanks to FMitzlaff).
  * Record linkage mode now more flexible: can implement different strategies for choosing optimal links  (with FMitzlaff).
  * Record linkage API refactored slightly to be more flexible (with FMitzlaff).
  * Added utilities for building equivalence classes from Duke output.
  * Made the XML config loader more robust.
  * Added a special cleaner for English person names.
  * Fixed bug in NumericComparator ([issue 66](https://code.google.com/p/duke/issues/detail?id=66))
  * Uses own Lucene query parser to avoid issues with search strings.
  * Upgraded to Lucene 3.5.0.
  * Added many more tests.
  * Many small bug fixes to core, NTriples reader, ec.

# Duke 0.3 #

This version of Duke introduces:

  * Refactored API to make it much more user-friendly.
  * Documented API to same end.
  * New [comparators](Comparator.md): NumericComparator, DiceCoefficientComparator, SoundexComparator
  * A new record linkage mode which can be used to link records from different data sets.
  * Numerous bug fixes and new test cases.
  * Performance improvements in the Levenshtein comparator.
  * Default cleaner now strips accents.
  * Upgraded to Lucene 3.3.0.
  * Version stamping in manifest file, API, and command-line client.

# Duke 0.2 #

This version of Duke fixes a number of bugs, and adds a number of improvements.

  * Example data and setup now included in distribution.
  * New [comparators](Comparator.md): JaroWinklerTokenized, DifferentComparator
  * A new DebugCompare command (see TuningGuide)
  * More flexibility in the CSV [data source](DataSources.md)
  * Better reporting of configuration errors
  * A --verbose option