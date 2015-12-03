# Introduction #

The configuration file is what controls how gets and processes data. It lists the data sources data is retrieved from, describes which columns to include, how to clean them, and how to compare data. The syntax is formally defined by [a RELAX-NG schema](http://code.google.com/p/duke/source/browse/src/main/resources/no/priv/garshol/duke/config-schema.rnc).

# Example #

Below is a complete example of a configuration file.

```
<duke>
  <schema>
    <threshold>0.82</threshold>
    <maybe-threshold>0.80</maybe-threshold>
    <path>test</path>

    <property type="id">
      <name>ID</name>
    </property>

    <property>
      <name>NAME</name> 
      <comparator>no.priv.garshol.duke.Levenshtein</comparator>
      <low>0.3</low>
      <high>0.88</high>
    </property>    
    <property>
      <name>MBOX_HASH</name>
      <comparator>no.priv.garshol.duke.ExactComparator</comparator>
      <low>0.47</low>
      <high>0.8</high>
    </property>    
    <property>
      <name>AFFILIATION</name>
      <comparator>no.priv.garshol.duke.ExactComparator</comparator>
      <low>0.47</low>
      <high>0.6</high>
    </property>    
    <property>
      <name>HOMEPAGE</name>
      <comparator>no.priv.garshol.duke.ExactComparator</comparator>
      <low>0.47</low>
      <high>0.9</high>
    </property>    
  </schema>  
  
  <ntriples>
    <param name="input-file" value="dogfood.ntriples"/>

    <column name="?uri"
            property="ID"/>
    <column name="http://xmlns.com/foaf/0.1/name"
            cleaner="no.priv.garshol.duke.LowerCaseNormalizeCleaner"
            property="NAME"/>
    <column name="http://xmlns.com/foaf/0.1/mbox_sha1sum"
            property="MBOX_HASH"/>
    <column name="http://swrc.ontoware.org/ontology#affiliation"
            property="AFFILIATION"/>
    <column name="http://xmlns.com/foaf/0.1/homepage"
            property="HOMEPAGE"/>

  </ntriples>
</duke>
```

The rest of this page goes through the details. For more examples, see ExamplesOfUse.

# Schema #

Duke compares two records by examining the properties one by one. The `property` elements describe the comparison of each property. The comparison is done by a Comparator (see that page for details). If the values compare exactly, Duke assumes the probability of the two records representing the same object to be the `high` probability. If the values are completely unlike, Duke assumes the probability is the `low` probability. If one of the records has no value for the property Duke considers that it hasn't seen any evidence and skips the property.

Note that a property can be declared to have `type="ignore"`, in which case Duke will not use it to compare records.

If two records have exactly the same name, but different values for the other three properties we get the probabilities 0.88, 0.47, 0.47, 0.47. Duke then uses Bayes's Theorem to combine these into a single probability, which gives 0.836. That's below the value given in `threshold`, and so Duke would consider the records different. It is above the `maybe-threshold`, however, so Duke would consider it a possibility that the two records match.

Note that the `low` probability should generally be in the range 0.0-0.5, while `high` should be 0.5-1.0. If a comparison leads to a probability of 0.0 or 1.0, then that's going to be the result for the entire record, as no further evidence will change this probability.

For guidance on configuring your data, see HowToModel.

The `path` is a reference to the directory in which the Lucene indexes will be created.

# Data sources #

The final part of the configuration file lists the DataSources used, and configure how to turn data from the sources into Duke records. Inside each data source element the `column` elements take one data value from the source, turns it into a Duke property, and cleans it.

The `name` attribute references a data element in the data source.

The `cleaner` attribute references a Cleaner used to normalize the data. Note that you can put in multiple cleaners (separated by spaces). Duke will run the cleaners in the order they're given.

The `property` attribute names the property to be created in the Duke record. If omitted it defaults to the value of `name`.

The `prefix` attribute defines a prefix to be added to the value when creating the Duke record. This is mainly used to disambiguate identifiers coming from different sources (such as different CSV files or database tables).

# Modes #

If the DataSources appear as in the example above, that is, directly within the `<duke>` element, Duke will run in DeduplicationMode.

If the DataSources are divided into two `<group>` elements, as in the example below, Duke will run in RecordLinkageMode.

```
<duke>
  <object class="no.priv.garshol.duke.comparators.NumericComparator"
          name="AreaComparator">
    <param name="min-ratio" value="0.7"/>
  </object>
  
  <schema>
    <threshold>0.7</threshold>

    <property type="id">
      <name>ID</name>
    </property>

    <property>
      <name>NAME</name> 
      <comparator>no.priv.garshol.duke.comparators.Levenshtein</comparator>
      <low>0.3</low>
      <high>0.88</high>
    </property>    
    <property>
      <name>AREA</name> 
      <comparator>AreaComparator</comparator>
      <low>0.2</low>
      <high>0.6</high>
    </property>
    <property>
      <name>CAPITAL</name> 
      <comparator>no.priv.garshol.duke.comparators.Levenshtein</comparator>
      <low>0.4</low>
      <high>0.88</high>
    </property>    
  </schema>

  <group>
    <csv>
      <param name="input-file" value="countries-dbpedia.csv"/>
      <param name="header-line" value="false"/>
      
      <column name="1" property="ID"/>
      <column name="2"
              property="NAME"
              cleaner="no.priv.garshol.duke.examples.CountryNameCleaner"/>
      <column name="3"
              property="AREA"/>
      <column name="4"
              property="CAPITAL"
              cleaner="no.priv.garshol.duke.examples.CapitalCleaner"/>
    </csv>
  </group>

  <group>
    <csv>
      <param name="input-file" value="countries-mondial.csv"/>
      
      <column name="id" property="ID"/>
      <column name="country"
              property="NAME"
              cleaner="no.priv.garshol.duke.examples.CountryNameCleaner"/>
      <column name="capital"
              property="CAPITAL"
              cleaner="no.priv.garshol.duke.cleaners.LowerCaseNormalizeCleaner"/>
      <column name="area"
              property="AREA"/>
    </csv>
  </group>
</duke>
```

This example tries to match countries from DBpedia with countries from Mondial. It will never produce matches for two countries both from DBpedia, or both for Mondial, and it tries to find one Mondial country as a match for every DBpedia country.

# Configuring objects #

It's possible to set bean properties on cleaners and comparators, by configuring the object first, and then referring to it later. To configure an object, include this at the top of the file, before the `<schema>` element:

```
 <object class="no.priv.garshol.duke.comparators.NumericComparator"
          name="AreaComparator">
    <param name="min-ratio" value="0.7"/>
  </object>
```

Once this is done you can refer to the configured object further down with:

```
      <comparator>AreaComparator</comparator>
```

# Configuring databases #

Duke can use different strategies for finding candidate matches, and this is dictated by which database implementation you use. To learn more about this, see DatabaseConfig.