# Introduction #

Here we use about [conference presenters at semantic web conferences](http://data.semanticweb.org/). This data is carefully produced from the conference proceedings of each conference, but still contains duplicate authors. We therefore use Duke in DeduplicationMode to identify duplicates.

# The configuration #

There are about 7500 presenters, data about whom are found in an NTriples file. We have an identifying URI for each, NAME, MBOX\_HASH, HOMEPAGE, and AFFILIATION. The MBOX\_HASH is not the email address (which is private information), but a SHA1 hash of the email address.

[The configuration file](http://code.google.com/p/duke/source/browse/doc/example-data/dogfood.xml) contains the setup, including mapping from NTriples to Duke records, cleaners, comparators, probabilities, etc. Note that most of the properties are compared exactly, as they are either identifiers or hashes.

[The test file](http://code.google.com/p/duke/source/browse/doc/example-data/dogfood-test.txt) contains a set of links carefully vetted by hand, often by Googling the people involved and checking their home pages, bibliographies, etc to make sure the links really are correct.

# Trying it out #

To test it, run this command

```
java no.priv.garshol.duke.Duke --testfile=dogfood-test.txt --testdebug --showmatches dogfood.xml
```

This will produce a long stream of output showing matches, including information about which matches are wrong, as shown below.

```
INCORRECT 0.8926911229347522
MBOX_HASH: '98f18d25daaf88121bf64a6ee85586b2787b0c2c', NAME: 'kewen wang', AFFILIATION: 'http://data.semanticweb.org/organization/griffith-university', 'http://data.semanticweb.org/organization/iiis-the-griffith-university', ID: 'http://data.semanticweb.org/person/kewen-wang', HOMEPAGE: 'http://www.cit.gu.edu.au/~kewen/', 
MBOX_HASH: '3f556bdfa966ad274010d5b0676446af36937113', '98f18d25daaf88121bf64a6ee85586b2787b0c2c', '998ed728cda197ddfd1f99c78ce8eb4874bc256c', AFFILIATION: 'http://data.semanticweb.org/organization/griffith-university', NAME: 'zhe wang', ID: 'http://data.semanticweb.org/person/zhe-wang', 

MATCH 0.9611650485436892
MBOX_HASH: 'e966302104bd52f060f0e4545e8299f2d54ee3ae', NAME: 'nigel r shadbolt', 'nigel r. shadbolt', 'nigel shadbolt', AFFILIATION: 'http://data.semanticweb.org/organization/university-of-southampton', ID: 'http://data.semanticweb.org/person/nigel-r-shadbolt', HOMEPAGE: 'http://users.ecs.soton.ac.uk/nrs/', 'http://www.ecs.soton.ac.uk/~nrs/', 
MBOX_HASH: 'e966302104bd52f060f0e4545e8299f2d54ee3ae', AFFILIATION: 'http://data.semanticweb.org/organization/university-of-southampton', NAME: 'nigel shadbolt', ID: 'http://data.semanticweb.org/person/nigel-shadbolt', HOMEPAGE: 'http://www.ecs.soton.ac.uk/~nrs/', 
```

Here we can see that the first match is wrong, mislead by the similarity of the names (Kewen Wang vs Zhe Wang), the affiliations being the same, and one email address being the same. The shared email address is probably a mistake.

Overall results are not too good, partly because the data are both full of errors and rather sparse:

```
Correct links found: 70 / 86 (81.3%)
Wrong links found: 3 / 16 (18.7%)
Unknown links found: 0
Percent of links correct 95.8%, wrong 4.1%, unknown 0.0%
Precision 95.8%, recall 81.3%, f-number 0.880503144654088
```