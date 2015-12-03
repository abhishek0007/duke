# Introduction #

This example links countries from [DBpedia](http://dbpedia.org) with countries from [the Mondial database](http://www.dbis.informatik.uni-goettingen.de/Mondial/). These databases share no common identifiers for countries, and so to link them we have to resort to using Duke in RecordLinkageMode.

We have [one CSV file dump from DBpedia](http://code.google.com/p/duke/source/browse/doc/example-data/countries-dbpedia.csv), with the columns ID, NAME, AREA, and CAPITAL. We also have [one from Mondial](http://code.google.com/p/duke/source/browse/doc/example-data/countries-dbpedia.csv), with the columns ID, NAME, CAPITAL, and AREA.

# Configuration #

The [actual configuration](http://code.google.com/p/duke/source/browse/doc/example-data/countries.xml) sets up mappings, cleaners, comparators and probabilities. You'll note that the threshold used is very low, at 0.63. This is because we are doing record linkage here, and so most poor matches are outcompeted by a better match for the same country. If we were doing deduplication these matches would be included, but now they are suppressed.

The correct matching results are found in [the test file](http://code.google.com/p/duke/source/browse/doc/example-data/countries-test.txt), which has been built manually.

Running the following command:

```
java no.priv.garshol.duke.Duke --testfile=countries-test.txt --testdebug --showmatches countries.xml
```

will produce detailed output showing all matches, all mistakes, and also statistics on overall performance. Here's a part of the output:

```
INCORRECT 0.7627088841882602
NAME: 'equatorial guinea', AREA: '28050', ID: '224', CAPITAL: 'malabo', 
NAME: 'guinea-bissau', AREA: '36125', ID: 'http://dbpedia.org/resource/Guinea-Bissau', CAPITAL: 'bissau', 

MATCH 0.9902338178797017
NAME: 'austria', AREA: '83850', ID: '13446', CAPITAL: 'vienna', 
NAME: 'austria', AREA: '83872', ID: 'http://dbpedia.org/resource/Austria', CAPITAL: 'vienna', 

MATCH 0.9901999953399144
NAME: 'solomon islands', AREA: '28450', ID: '1127', CAPITAL: 'honiara', 
NAME: 'solomon islands', AREA: '28400', ID: 'http://dbpedia.org/resource/Solomon_Islands', CAPITAL: 'honiara', 

NO MATCH FOR:
NAME: 'netherlands antilles', AREA: '960', ID: '6973', CAPITAL: 'willemstad', 
```

As you can see, matching Guinea-Bissau with Equatorial Guinea is wrong. The next two matches are correct, however. And finally, finding no match for the Netherlands Antilles is also correct.

The summary at the end says:

```
Correct links found: 204 / 217 (94.0%)
Wrong links found: 2 / 12 (16.6%)
Unknown links found: 0
Percent of links correct 99.0%, wrong 0.9%, unknown 0.0%
Records with no link: 25
Precision 99.0%, recall 94.0%, f-number 0.9645390070921986
```

Feel free to tweak the configuration to see if you can do better.