# Introduction #

There are three different repositories you need to know about:

  * [The old Duke repository](http://duke.googlecode.com/hg/maven-repository/), has versions 0.4 and 0.5
  * [Sonatype staging](https://oss.sonatype.org/content/repositories/snapshots/), has 0.6-SNAPSHOT, 0.7-SNAPSHOT, and 1.1-SNAPSHOT.
  * Maven central, which has versions 0.6 and 1.0.

# What to put in your pom.xml #

Add this to the dependencies part:

```
    <dependency>
      <groupId>no.priv.garshol.duke</groupId>
      <artifactId>duke</artifactId>
      <version>1.0</version>
    </dependency>
```

If you are using historical or snapshot versions, you must also add a repository to the pom.xml, as linked above.