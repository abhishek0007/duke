# Introduction #

A comparator can compare two string values and produce a
similarity measure between 0.0 (meaning completely different) and
1.0 (meaning exactly equal). These are used because we need
something better than simply knowing whether two values are the
same or not. Also, different kinds of values must be compared
differently, and comparison of complex strings like names and
addresses is a whole discipline in itself.

A number of comparators are provided with Duke, but it's trivial to
implement your own by implementing the [no.priv.garshol.duke.Comparator](http://www.garshol.priv.no/download/duke/no/priv/garshol/duke/Comparator.html)
interface.

All comparators are in the package `no.priv.garshol.duke.cleaners`.  The [javadoc for the package](http://www.garshol.priv.no/download/duke/no/priv/garshol/duke/comparators/package-summary.html) has more details.

# String comparators #

## Levenshtein ##

Uses Levenshtein edit distance to compute the similarity.
Basically, it measures the number of edit operations needed to
get from string 1 to string 2.

If in doubt, use this comparator.

## WeightedLevenshtein ##

A configurable version of Levenshtein where edit operations can
be assigned different weights. Very useful in cases where numbers
make up part of the string, and differences in the numbers matter
more than differences in letters. Addresses are one example of
this, because Main Street 12 and Main Street 14 are very different.

**estimator**: takes an instance of the estimator interface, which is
used to compute the actual edit operation weights.

## JaroWinkler ##

The Jaro-Winkler similarity measure, which many studies have
found to be the best available general string comparator for
deduplication. Use this for short strings like given names and
family names. Not so good for longer, general strings.

## QGramComparator ##

Uses q-grams comparison. It seems to be similar to Levenshtein, but a bit more eager to consider strings the same, and doesn't care what order tokens are in (with BASIC tokenizer). So for strings consisting of tokens that may be reordered it may be a better alternative than Levenshtein.

**q**: The value of `q`, that is, the size of the q-grams. Default = 2.

**formula**: Which formula to use to compare sets of q-grams from the two strings. Alternatives: OVERLAP, DICE, JACCARD. Default: OVERLAP.

**tokenizer**: Determines what kind of q-gram to produce. Alternatives: BASIC, POSITIONAL (includes the position of the q-gram), and ENDS (like BASIC, but includes first and last characters). Default: BASIC.

# Simple comparators #

## ExactComparator ##

Reports 0.0 if the values are not equal and 1.0 if they are.

## DifferentComparator ##

Reports 1.0 if the values are not equal and 0.0 if they are (the
inverse of ExactComparator).

# Specialized comparators #

## GeopositionComparator ##

Compares latitude/longitude positions by the distance between
them along the earth's surface. Expects values of the form "60.21,23.55" in WGS84,
with latitude first, and longitude second.

**max-distance**: the distance (in meters) at which positions are
considered entirely different. Defaults to 100 meters.

## NumericComparator ##

Compares two numbers. Returns the ratio of the smaller number to
the greater.

## PersonNameComparator ##

A rule-based comparator which understands the structure of
personal names. It knows about things like initials, middle
names, and that sometimes given and family name get reversed.
Uses Levenshtein to account for typos.

# Phonetic comparators #

## SoundexComparator ##

Compares terms using Soundex. If two terms have the same Soundex
code they are considered equal (1.0), if not they are
different (0.0).

## MetaphoneComparator ##

Similar to Soundex, but using the finer-grained Metaphone code
scheme.

## NorphoneComparator ##

A Norwegian version of Metaphone for Norwegian names.

# Token set comparators #

## DiceCoefficientComparator ##

Computes the Dice coefficient of two tokenized strings. Tokens
are compared using exact comparison by default, but any
comparator can be used to compare tokens.

## JaccardIndexComparator ##

Computes the Jaccard index of two tokenized strings. Tokens are
compared using exact comparison by default, but any comparator
can be used to compare tokens.