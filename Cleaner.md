# Duke has moved to [Github](https://github.com/larsga/Duke) #

# Introduction #

A cleaner's job is to make comparison easier by removing from
data values all variations that are not likely to indicate
genuine differences. For example, a cleaner might strip
everything except digits from a zip code. Or, it might normalize
and lowercase addresses. Or translate dates into a common format.

A number of cleaners are provided with Duke, but it's trivial to
implement your own by implementing the no.priv.garshol.duke.Cleaner
interface. If you want to use regular expressions to clean data you
can subclass the `AbstractRuleBasedCleaner`.

All cleaners are found in the package `no.priv.garshol.duke.cleaners`.  [The package javadoc](http://www.garshol.priv.no/download/duke/no/priv/garshol/duke/cleaners/package-summary.html) has more details.

## Basic string cleaners ##

### LowerCaseNormalizeCleaner ###

The default cleaner for string values.  It lowercases all
letters, removes whitespce at beginning and end, and normalizes
whitespace in between tokens. It also removes accents, turning é
into e, and so on.

### DigitsOnlyCleaner ###

This cleaner removes everything which is not a digit, and could
have been used to clean the zip code as described above.

### TrimCleaner ###

Simply trims whitespace at the beginning and end of the input string.

## Configurable cleaners ##

### RegexpCleaner ###

This cleaner uses regular expressions to do the cleaning.
Basically, it matches the input against the regular expression
it's given, and returns the part matching group number 1 (by
default), or the given group. If the expression does not match,
it returns null.

**regexp**: The regular expression to use. Must contain at least one group.

**group**: The number of the group to extract. 1 by default.

### MappingFileCleaner ###

Uses a mapping file to map incoming values to new values.

**mapping-file**: The name of a CSV file containing the mapping.
Column 1 has from values, column 2 to values.

### GenericValueCleaner ###

Uses a sub-cleaner to clean values first, then removes common
meaningless values. (For example, if many people have the phone
number 999-999-9999, this can be used to remove it.)

**generic**: The generic value to remove.

**sub-cleaner**: Another cleaner, used to clean data before generic
values are removed.

## Parsing cleaners ##

### PhoneNumberCleaner ###

This cleaner understands the syntax of phone numbers and will normalize phone numbers to a standard format (+CC number, where CC = country code). It knows the codes and syntax for phone numbers for some countries, and can easily be extended to support more countries.

### PersonNameCleaner ###

_Experimental_ cleaner for English person names (translates "Sam"
to "samuel" etc).

### NorwegianAddressCleaner ###

A rule-based cleaner which can normalize Norwegian addresses. It
understands abbreviations like "gt" for "gate", "pb"
for "postboks", and so on.

### NorwegianCompanyNameCleaner ###

A rule-based cleaner which can normalize Norwegian company names.
It normalizes "a/s" to "as", and performs other normalizations as
well.