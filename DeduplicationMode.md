# Introduction #

In deduplication mode, all records are matched against all other records to find potential duplicates. This is the mode you'll use if you want to process an address book to find duplicate entries, or find duplicate registrations across the `Supplier` and `Customer` tables in a database.

To use it with the [XMLConfig](XMLConfig.md), just write a normal configuration with a flat list of DataSources.

When [UsingTheAPI](UsingTheAPI.md), call Processor.deduplicate() to run in deduplication mode.

The alternative is RecordLinkageMode.