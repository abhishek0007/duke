**NOTE:** This page is about a fairly specialized use case that's common in Norway, but probably not anywhere else. So unless you are dealing with the Norwegian national curriculum you can probably ignore this page.

The Norwegian national curriculum is published electronically in
various machine-readable formats. This is used by publishers and
others to connect educational content with the various parts of
the curriculum. However, the Ministry of Education regularly
publishes new curricula, requiring content to be reconnected with
the new curriculum.

Knowledge goals in the new curriculum which have the exact same
wording as in the old curriculum are easy to deal with. But what
about the ones that have been changed, but still retain the same
meaning? If publishers could connect up those automatically they
could save considerable extra effort.

As it turns out, with Duke this is actually easy. Here's what you
need to do:

  1. Export all knowledge goals in the old curriculum to a CSV
> > file with the columns (ID, TEXT, AREA, TRINN, FAG). ID is
> > some identifier for the goal, TEXT is the actual content of
> > it, AREA is the hovedområde, and TRINN and FAG are
> > identifiers for the årstrinn and subject, respectively.


> 2. Do the same with the new curriculum you want to map.

> 3. Run the command below, using the config file below.

The command is:

```
java no.priv.garshol.duke.Duke --singlematch --linkfile=link.txt config.xml`
```

You'll now have a text file containing pairs of IDs for the
knowledge goals that match. That's it. If you want to be more
fancy you can configure Duke to run directly against your
database, and use the Duke API to write directly back to your
database.

By tweaking the configuration you can make it be more careful
about which goals to match, for example by raising the threshold,
or by switching TEXT comparison to Levenshtein.

Anyway, I hope this is of use.

```
<duke>
  <object class="no.priv.garshol.duke.cleaners.LowerCaseNormalizeCleaner"
          name="lcncleaner"/>
  
  <schema>
    <threshold>0.4</threshold>

    <property type="id">
      <name>ID</name>
    </property>
    <property>
      <name>NAME</name> 
      <comparator>no.priv.garshol.duke.comparators.DiceCoefficientComparator</comparator>
      <low>0.35</low>
      <high>0.95</high>
    </property>    
    <property>
      <name>AREA</name> 
      <comparator>no.priv.garshol.duke.comparators.ExactComparator</comparator>
      <low>0.4</low>
      <high>0.5</high>
    </property>    
    <property>
      <name>TRINN</name> 
      <comparator>no.priv.garshol.duke.comparators.ExactComparator</comparator>
      <low>0.3</low>
      <high>0.5</high>
    </property>    
    <property>
      <name>FAG</name> 
      <comparator>no.priv.garshol.duke.comparators.ExactComparator</comparator>
      <low>0.0</low>
      <high>0.5</high>
    </property>    
  </schema>  

  <group>
    <csv>
      <param name="input-file" value="nyplan.csv"/>
      <param name="header-line" value="false"/>

      <column name="1" property="ID"/>
      <column name="2" property="NAME" cleaner="lcncleaner"/>
      <column name="3" property="AREA" cleaner="lcncleaner"/>
      <column name="4" property="TRINN"/>
      <column name="5" property="PLAN"/>
    </csv>
  </group>

  <group>
    <csv>
      <param name="input-file" value="gammelplan.csv"/>
      <param name="header-line" value="false"/>

      <column name="1" property="ID"/>
      <column name="2" property="NAME" cleaner="lcncleaner"/>
      <column name="3" property="AREA" cleaner="lcncleaner"/>
      <column name="4" property="TRINN"/>
      <column name="5" property="PLAN"/>
    </csv>
  </group>
</duke>
```