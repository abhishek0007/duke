Let's say that we're working with a customer database, and trying
to identify duplicate customer records. Assume we have these
three rows:

| ID | NAME              | ADDRESS         | ZIP   | EMAIL |
|:---|:------------------|:----------------|:------|:------|
| 1  | J. Random Hacker   | Main St 101     | 21231 |       |
| 2  | John Random Hacker | Mian Street 101 | 21231 | hack@gmail.com |
| 3  | Jacob Hacker       | Main Street 201 | 38122 | jacob@hotmail.com |

First, Duke will read the data from some DataSources, and create
internal Duke Record objects for each record in the database. As
part of that job, the data will be cleaned. After cleaning, the
data will look as follows.

| ID | NAME              | ADDRESS         | ZIP   | EMAIL |
|:---|:------------------|:----------------|:------|:------|
| 1  | j. random hacker   | main street 101 | 21231 |       |
| 2  | john random hacker | mian street 101 | 21231 | hack@gmail.com |
| 3  | jacob hacker       | main street 201 | 38122 | jacob@hotmail.com |

Then, Duke will do a detailed comparison of the records to see
what the chances are that they represent the same customer. We use the following configuration:

```
  <schema>
    <threshold>0.732</threshold>

    <property type="id">
      <name>ID</name>
    </property>
    <property>
      <name>NAME</name> 
      <comparator>no.priv.garshol.duke.comparators.QGramComparator</comparator>
      <low>0.35</low>
      <high>0.88</high>
    </property>    
    <property>
      <name>ADDRESS1</name> 
      <comparator>address-comp</comparator>
      <low>0.25</low>
      <high>0.65</high>
    </property>    
    <property>
      <name>EMAIL</name> 
      <comparator>no.priv.garshol.duke.comparators.ExactComparator</comparator>
      <low>0.4</low>
      <high>0.8</high>
    </property>    
    <property>
      <name>ZIP</name> 
      <comparator>no.priv.garshol.duke.comparators.ExactComparator</comparator>
      <low>0.45</low>
      <high>0.6</high>
    </property>    
  </schema>  
```

Note the comparator named `address-comp`. That's a reference to an object instantiated above `schema`, like this. In general, you can create any object using the syntax below, and then always refer to it wherever you can refer to a class.

```
  <object class="no.priv.garshol.duke.comparators.WeightedLevenshtein$DefaultWeightEstimator"
          name="estimator">
    <param name="digit-weight" value="10.0"/>
  </object>
  <object class="no.priv.garshol.duke.comparators.WeightedLevenshtein"
          name="address-comp">
    <param name="estimator" value="estimator"/>
  </object>
```

To return to the `schema` fragment above, the threshold says that unless the probability that the records represent the same thing is computed to be 0.732 (that is, 73.2%) or higher, we will not consider the records the same. The `high` and `low` elements give the probability to be used if the property values are exactly the same, or entirely different, respectively. Properties that lack a value in one of the records are ignored.

The comparison of records 1 and 2 comes out like this:

```
---ADDRESS1
'main street 101' ~ 'mian street 101': 0.867 (prob 0.6127)
Result: 0.5 -> 0.6127

---NAME
'j. random hacker' ~ 'john random hacker': 0.8 (prob 0.78542)
Result: 0.6127 -> 0.8527

---ZIP
'21231' ~ '21231': 1.0 (prob 0.6)
Result: 0.8527 -> 0.8967

Overall: 0.8967
```

We start by comparing the addresses, which are fairly similar, with a similarity of 0.87. That gives us a probability of 0.61 of the records being the same. Then we compare the names, which are also very similar, so the probability from this property is 0.79. We then combine that with the probability from the address (0.61), to get a new probability of 0.85. The zip codes are also the same, which takes us even higher, and we wind up with 0.897, which is well over 0.732, and so Duke considers the records to represent the same person.

If we compare records 1 and 3 it comes out rather differently:

```
---ADDRESS1
'main street 101' ~ 'main street 201': 0.73 (prob 0.58)
Result: 0.5 -> 0.58

---NAME
'j. random hacker' ~ 'jacob hacker': 0.6 (prob 0.64)
Result: 0.58 -> 0.71

---ZIP
'21231' ~ '38122': 0.0 (prob 0.45)
Result: 0.71 -> 0.67

Overall: 0.67
```

The addresses are very similar, but we are using weighted Levenshtein for the comparison, so the one-digit difference counts quite a lot. As a result, we get a pretty low probability: 0.58. The names are not entirely different, but fairly different, so this takes us no higher than 0.71. The zip codes, however, are entirely different, and so the score goes down again, to 0.67. That's below our threshold, and the records are considered different.

I hope this helps show how Duke weighs the evidence from different fields and uses it to reach a verdict.