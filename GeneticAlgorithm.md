# Introduction #

Creating an accurate configuration can be tricky, particularly if
you're new to Duke. To help you solve this, Duke comes with a
genetic algorithm that can automatically tune your configuration
for you. This may sound like magic, but it actually does work.

Note that the genetic algorithm does not understand Duke at all,
and so it can produce configurations that are quite ... strange.
That is, typically the configuration that comes out will work,
but it may seem nonsensical, and it may be difficult to tune
manually afterwards.

# Understand #

There are two ways to run the genetic algorithm:

  * **Passive mode** requires that you have a test file containing all the correct matches for your data set. If that's difficult to come by you can make a subset of your data for training purposes, and create a test file for that.

  * **Active mode** makes do without a test file by asking you whether matches are correct or not. It has less information to work with than the passive mode, but tries to make up for that by only asking for the most informative potential matches. By default it asks you 10 questions per generation, but you can tune that manually.

If you can, use passive mode, as it means you can let the genetic
algorithm run unattended, and it produces better results.

# Trying it out #

If you want to see the genetic algorithm in action, you can try it on the data sets that come with Duke. To try it with the LinkingCountries example, just run

```
java no.priv.garshol.duke.genetic.Driver countries.xml
```

# How to get started #

To get off the ground you need a complete Duke configuration.
That is, you must have data sources, cleaning, and comparison set
up. The threshold, comparators, and high and low probabilities
don't have to make any sense, but you do have to set them up.
This is so Duke knows what properties are in the schema.

## Active mode ##

If you don't have a test file, simply run the genetic algorithm
as follows:

```
  java no.priv.garshol.duke.genetic.Driver --output=genetic.xml config.xml
```

This will run in active learning mode, and output the best
configuration to genetic.xml after each generation. You can stop
the process any time you want, and try out the configuration.

Note that with this approach the genetic algorithm will forget
all your answers, so if you run it again you may have to answer
the same questions over again. To avoid this, you can run it like
this:

```
  java no.priv.garshol.duke.genetic.Driver --output=genetic.xml --linkfile=links.txt config.xml
```

Now all your answers will be recorded in links.txt. If you stop
the algorithm and want to run it again you can run it with:

```
  java no.priv.garshol.duke.genetic.Driver --output=genetic.xml --linkfile=links.txt --testfile=links.txt --active config.xml
```

Now it will remember what you told it last time, but it will
still ask you 10 questions for each generation. It will also
assume that there exist correct links which are not in your test
file. Any new answers will be added to the end of the link file,
so you can rerun with these parameters many times.

## Passive mode ##

If you do have a test file, simply run Duke like this:

```
  java no.priv.garshol.duke.genetic.Driver --output=genetic.xml --testfile=links.txt config.xml
```

With these parameters Duke will not ask you any questions, and it
will keep running, assuming all correct links are in the test
file, and that all links which are not in there are wrong.

# Performance #

Running the genetic algorithm with a population of 100 configurations for 100 generations (which is the default) means running Duke 10,000 times. If each run takes a second that means 2 hours and 45 minutes to do the whole thing. It follows that running the genetic algorithm on a big data set is not really going to work very well.

If you have a big data set you'll need to make a subset and run the genetic algorithm on that. Take care in selecting the subset, because if there are any quirks in the subset the algorithm will adapt to those. The best approach is to use a random number generator and select the records to look at completely by random. Otherwise you run the risk that the algorithm will give you a configuration that works perfectly on the subset and poorly on the complete data set.

You may want to set the `--threads=N` parameter to the number of CPU cores you have in order to speed things up.

See also the PerformanceTuning page.

# Background #

A blog post [describing the genetic algorithm](http://www.garshol.priv.no/blog/225.html) and how it works.

Another blog post [on the active learning approach](http://www.garshol.priv.no/blog/262.html).

# Feedback #

This is a completely new feature, and I'm not sure if anyone has
actually tried it, so feedback is very welcome.