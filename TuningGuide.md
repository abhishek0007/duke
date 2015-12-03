# Getting started #

The first time you run Duke you may want to do as follows:

```
java no.priv.garshol.duke.Duke --showmatches config.xml
```

This will show you the matches produced by Duke as they are found, and will give you a chance to tweak the configuration until the Duke records and the matches between them start to make sense.

# Fine-tuning #

Once you have something that's beginning to make sense you may want to make a test file. This is a file that tells Duke which matches are correct and which are incorrect. Basically it takes the following form:

```
+id1,id2
-id1,id3
+id4,id2
```

Lines beginning with + indicate correct matches; those beginning with - indicate incorrect matches.

You can get Duke to help you create a test file. Generally, it's best to do this for only a subset of your data, since manually checking every match can take a long time. You may want to lower the threshold for this, too, to make sure that you find all matches. Anyway, run Duke like this, to get it to write the test file for you, and ask you for each match whether it's correct:

```
java no.priv.garshol.duke.Duke --interactive --linkfile=test.txt --showmatches config.xml
```

Running Duke again with

```
java no.priv.garshol.duke.Duke --testfile=test.txt --testdebug config.xml
```

will display only matches which are incorrect or unknown. At the end you also get statistics on the quality of the matching.

This should help you see the effect of configuration changes, and also makes it easier to work with the results, since only new results (ie: those not in the test file) are displayed, removing a lot of cognitive noise.

# Understanding record matching #

If you want to know why two records compared the way they did you can use this command:

```
  java no.priv.garshol.duke.DebugCompare <xmlfile> <id1> <id2>
```

It will show the similarity scores for each property and the overall probability computed from them.

Note that for DebugCompare to work you must have a Lucene index on disk (use the `<path>` element in the config file), in order for there to be a Lucene index for DebugCompare to look the items up in.