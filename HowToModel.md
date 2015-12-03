# Properties to include #

In general, you want properties which should have the same values if the records represent the same thing. These help you work out which records represent the same thing. Names are always useful for this.

Let's say we're looking at your address book and trying to find duplicates there. People's names, email addresses, home addresses, and phone numbers are all useful properties to include. If any of these have the same values, that's an indication that the records may represent the same person. The date the record was last updated is a useless property, on the other hand, because it provides no evidence one way or another.

Note that it may also be useful to include properties which do not provide evidence of equality, but which does provide evidence of difference. For example, for companies, including the country field may be useful. If two companies are in the same country that doesn't mean they are the same, but if they are in different countries that means they're almost definitely different. Thus you may give this field probabilities of 0.5 (high) and, say, 0.1 (low), and use the property to distinguish records which might otherwise have caused false positives.

# Independence #

Duke assumes that the different properties are independent of each other. That is, it assumes that if the values for property #1 are the same, the chances of the values of property #2 being the same do not increase. So if you are comparing records with addresses, for example, you should not include both 'zip code' and 'city'. If two records have the same zip code they'll be almost guaranteed to have the same city value, which will cause the probability of them being the same to be exaggerated. The same goes for 'given name', 'family name', and 'name'. Either use just the first two, or just the last one.

# Probabilities #

Coming up with the right probabilities for the different properties is difficult, and the only way to do it at the moment is to do an initial estimate based on gut feeling, and then iteratively refine it by running Duke and looking at the results. (The TuningGuide has more help on this.) In general, however, you should be wary of assigning probabilities above 0.95 unless you have perfect IDs (in which case you probably don't need Duke at all).

# Null #

If one record has no value for a property this is not considered evidence. So if property X has probabilities 0.3 and 0.6, and record #1 has no value while record #2 has 'foo' as the value, Duke will draw no conclusions from this at all.

The same applies if neither record #1 nor record #2 have any values. In that case Duke will not say that null == null and use the high probability. Instead, it will ignore the property and not consider this evidence one way or the other.

# Multiple values #

All properties are multi-value properties in Duke, so this is straightforward. Simply make records which have multiple values, and Duke will compare all pairs of values. At the moment, the default behaviour is to look only at the most similar pair of values, and ignore all others. Other behaviours have been considered, and could be implemented if anyone needs them.

The NTriples, SPARQL, and CSV DataSources support multiple properties, while JDBC ([issue 126](https://code.google.com/p/duke/issues/detail?id=126)) does not yet.