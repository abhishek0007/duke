# Introduction #

This example connects data about authors from the library catalog
of [the Deichman Library](http://www.deichmanske-bibliotek.oslo.kommune.no/) in Norway with person data from [DBpedia](http://dbpedia.org).
DBpedia contains information about roughly 800,000 people,
whereas the library data set has roughly 200,000 people. There
are no common identifiers. The library has published some
[additional background](http://digital.deichman.no/blog/2012/02/24/data-deichman-–-deichman-data-i-ny-drakt/).

The properties we use for matching are NAME, YEAROFBIRTH,
YEAROFDEATH, and NATIONALITY. Except for NAME these are rather
sparsely populated, so we don't have much to work with, but this
really is all there is.

**Note:** This particular example requires changes made since the Duke 0.4 release, so if you want to run this example you'll have to build it from source.

# Getting the data, part 1 #

The data sets are quite big, so we do not store them in the
source code repository, and so you have to download them
yourself. Instructions for how to do it on Linux and MacOS X are
provided here. I'm sure there are ways to do it on Windows, too,
but I'm not that familiar with Windows, and so do not supply any.

First, let's get the library data. Run the following command:

```
wget "http://data.deichman.no/sparql?query=CONSTRUCT%20{?s%20?p%20?o.}%20WHERE%20{%20GRAPH%20<http://data.deichman.no/books>%20{?s%20a%20foaf:Person%20;%20?p%20?o%20.%20}}&format=text/plain"
```

It may look frightening, but what it does is to use a [SPARQL](http://en.wikipedia.org/wiki/SPARQL)
query to retrieve all the person data from a particular data set
at the library. Once this is done you have the data in NTriples
format.

In order to load it incrementally we have to sort the file, so
that all properties of one object appear together. We can load it
without doing this, too, but then the entire file has to be
loaded at once, which is a pain. So, please run:

```
sort sparql?... > deich-person.ntriples
```

You can now delete the sparql file.

# Getting the data, part 2 #

The next step is to get the DBpedia data. These data are already
provided for us as a download, which makes it easier. We need the
persondata NT files from: http://wiki.dbpedia.org/Downloads37#h167-1

To download, unpack, sort, and set everything up, please run the
following:

```
wget http://downloads.dbpedia.org/3.7/en/persondata_en.nt.bz2
wget http://downloads.dbpedia.org/3.7/de/persondata_de.nt.bz2
bunzip2 persondata_en.nt.bz2
bunzip2 persondata_de.nt.bz2
cat persondata_en.nt persondata_de.nt | sort | uniq > persondata.ntriples
```

# The configuration #

The [actual configuration](http://code.google.com/p/duke/source/browse/doc/example-data/deichmann.xml) sets up the mappings and all that.
Note the reference to a file path for storing the Lucene index.
In this case the data sets are so big that it's best to put the
index on disk. We also use a rather high threshold (at 0.77),
because many authors don't have a match in DBpedia, and the data
sets are so big that there are always people with similar names
in DBpedia.

To run it, do

```
java no.priv.garshol.duke.Duke --progress --showmatches deichmann.xml
```

This will first index up the 800,000 records from DBpedia (which
is fairly quick), then start spewing out a massive amount of
text.  Watch as much of this as you feel like, then stop the
process.

Here is an example of the output:

```
NO MATCH FOR:
NAME: 'sosuna asefa getahun', ID: 'http://data.deichman.no/person/Asefa_Getahun_Sosuna', NATIONALITY: 'http://dbpedia.org/resource/Ethiopia', 

MATCH 0.7806788511749347
NAME: 'ivar asen', YEAROFBIRTH: '1813', ID: 'http://data.deichman.no/person/Asen_Ivar_1813-96', NATIONALITY: 'http://dbpedia.org/resource/Norway', 
YEAROFDEATH: '1896', NAME: 'ivar aasen', 'ivar aasen', YEAROFBIRTH: '1813', ID: 'http://dbpedia.org/resource/Ivar_Aasen', 

MATCH 0.8125
NAME: 'duygu asena', YEAROFBIRTH: '1946', ID: 'http://data.deichman.no/person/Asena_Duygu_1946-', 
YEAROFDEATH: '2006', NAME: 'duygu asena', 'duygu asena', YEAROFBIRTH: '1946', ID: 'http://dbpedia.org/resource/Duygu_Asena', 
```

There's no manually vetted test file in this case, because Duke
finds about 50,000 links, and that's just way too many to check
manually.