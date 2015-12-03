# Introduction #

In record linkage mode, Duke takes two sets of records, and for each record in one set it tries to find one matching record in the other set. If you have two databases, each with a 'Country' table, and want to set up a mapping table between them, this is the mode you want to use.

In record linkage mode, Duke will _not_ attempt to find duplicates within each set. For that you want DeduplicationMode.

In the [XMLConfig](XMLConfig.md), set up record linkage mode by separating your DataSources into two `<group>` elements.

When [UsingTheAPI](UsingTheAPI.md), call the Processor.link method.