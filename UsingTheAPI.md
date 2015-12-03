# Using the API #

The first thing you need is a populated `Configuration` object (all classes mentioned here are in the `no.priv.garshol.duke` package). You can do this through the API, but the easiest way is to write an [XML configuration file](XMLConfig.md) and then load it by calling `ConfigLoader.load(filename)`. Once that's done you need a `Processor`, which you can instantiate with the constructor `new Processor(config)`.

Before starting the processor you need to register a MatchListener so that the processor will inform you when it finds matches. The easiest way to implement one is to subclass `AbstractMatchListener`.

Now you're ready. To find duplicates, call `Processor.deduplicate()`. To link across groups of data sources, call `Processor.link()`.

There's more to the API, of course, but this is the basics.

# An example #

Here's a trivial example of a command-line client that loads the config file pointed to on the command-line and prints match information to standard out.

```
import no.priv.garshol.duke.*;
import no.priv.garshol.duke.matchers.PrintMatchListener;

public class RunDuke {

  public static void main(String[] argv) throws Exception {
    Configuration config = ConfigLoader.load(argv[1]);
    Processor proc = new Processor(config);
    proc.addMatchListener(new PrintMatchListener(true, true, true, false,
                                                 config.getProperties(),
                                                 true));
    proc.deduplicate();
    proc.close();
  }
  
}
```

# In the driver's seat #

In the example above, Duke does all the work, reading from the data sources and processing the records. If you want, you can run the driving loop yourself.

To do that, you need to build collections of Record objects. You can write your own implementation of the interface, or just use RecordImpl, which is a simple Map-based implementation. Once you have a good-sized batch of records, call `Processor.deduplicate(batch)` and pass in the records. They will be indexed and matched, and any matches passed to the listener. You can repeat the call as many times as needed.

Note that in this case Duke doesn't clean the data, but simply processes them directly. So any cleaning will need to be done beforehand.

# Javadoc #

The full javadoc is [hosted off-site](http://www.garshol.priv.no/download/duke/overview-summary.html).

# Maven repository #

To include Duke in your Maven project, see the MavenSetup page.