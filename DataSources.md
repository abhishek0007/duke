# Introduction #

All data to be deduplicated or linked by Duke must come from a data source. A data source is basically a class which implements the DataSource interface, which is just a single method. Below we describe all the currently supported data sources, but it is easy to implement and plug in your own data source.

Note that you can have any number of data sources in the same XML configuration file.

Note that if you use ElasticSearch there is [a separate project](https://github.com/YannBrrd/elasticsearch-entity-resolution) providing a plug-in for using Duke to deduplicate data in ElasticSearch.

# The built-in data sources #

All the data sources are implemented by classes in the `no.priv.garshol.duke` package.

## CSV data source ##

This source can read CSV files. Below is an example configuration:

```
<csv>
  <param name="input-file" value="customers.csv"/>

  <column name="ID"/>
  ...
</csv>
```

Unless `header-line` is set to `false` this source assumes the first line not to be skipped is a row of column names. These can be used by the column references inside the `csv` element.

Columns can also be referred to using their indexes, with the first column referred to as `1`. If there is no header line this is the only way to refer to columns.

The source assumes values are separated by commas and that doublequotes are used around strings.

The configuration parameters are:

  * **input-file**: refers to the CSV file
  * **encoding**: the character encoding of the file
  * **skip-lines**: number of initial lines to skip over
  * **header-line**: is there a header line with column names? (**true**|false)
  * **separator**: column separator character (',') _in Mercurial, coming in version 1.2_

If a CSV column can contain multiple values separated textually, configure the column with

```
  <column name="EMAIL" split-on=";" .../>
```

where the `split-on` attribute contains a regular expression to split the column on. Note that cleaning is applied to each individual value after splitting.

## JDBC data source ##

This source can load data from a database over a JDBC connection. Below is an example configuration:

```
  <jdbc>
    <param name="driver-class" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
    <param name="connection-string" value="jdbc:sqlserver://127.0.0.1"/>
    <param name="user-name" value="user"/>
    <param name="password" value="secret"/>
    <param name="query" value="
        select * from CUSTOMERS
      "/>

    <column property="ID"/>
    ...
  </jdbc>
```

The source connects to the database over JDBC, then issues the given SQL query, and the column references refer to the column names in the SQL result set.

The configuration parameters are:

  * **driver-class**: the Java class name of the JDBC driver (depends on database)
  * **connection-string**: the JDBC connection URL
  * **user-name**: the user name to connect as
  * **password**: the password
  * **query**: the query to use to retrieve data

If you need to split column values into multiple values, use the same solution as for CSV data (see above).

## JNDI data source ##

This is the same as the JDBC data source, except that it gets an already configured JDBC connection via JNDI:

```
  <jndi>
    <param name="jndi-path" value="jdbc/CatalogDB"/>
    <param name="query" value="
        select * from CUSTOMERS
      "/>

    <column property="ID"/>
    ...
  </jndi>
```

The configuration parameters are:

  * **jndi-path**: the path to the JNDI resource
  * **query**: the query to use to retrieve data

## NTriples data source ##

The NTriples data source can read files in the NTriples format, which is a simple serialization format for RDF. We support only this format at the moment, in order to avoid having to add a dependency on an RDF engine, which would require a large number of .jar files to be added.

By default this data source will load the complete NTriples file into memory before processing begins, in order to collect all properties of a resource into a single record. If the NTriples file is sorted you can load in incremental mode instead, which keeps only one record in memory at a time.

Below is an example configuration:

```
  <ntriples>
    <param name="input-file" value="dogfood.ntriples"/>

    <column name="?uri" property="ID"/>
    <column name="http://xmlns.com/foaf/0.1/name"
            cleaner="no.priv.garshol.duke.LowerCaseNormalizeCleaner"
            property="NAME"/>
    ...
  </ntriples>
```

Note that special column name "?uri" which is used to add the URI of the resource to the created record. Other column names refer to the URIs of RDF properties.

The configuration parameters are:

  * **input-file**: the NTriples file to load
  * **accept-types**: the URI of the class to process (in case the file contains data about resources of more than one class, but you only want to process a single class)
  * **incremental-mode**: set to true if the file is sorted

## SPARQL data source ##

The SPARQL data source can load data via SPARQL queries to a SPARQL endpoint. It requires queries to return a tabular result with the resource URI as the first column, the property as the second column, and the value as the third column. Alternatively, you can set triple-mode to false, and map each result row to a record.

Below is an example configuration in triple-mode:

```
  <sparql>
    <param name="endpoint" value="http://localhost:8080/sparql/"/>
    <param name="query" value="
      SELECT DISTINCT ?instance ?p ?o
      WHERE {
        ?instance a &lt;http://xmlns.com/foaf/0.1/Person> .
        ?instance ?p ?o .
      }
      ORDER BY ?instance ?p ?o
    "/>

    <column name="?uri"
            property="ID"/>
    <column name="http://xmlns.com/foaf/0.1/name"
            cleaner="no.priv.garshol.duke.LowerCaseNormalizeCleaner"
            property="NAME"/>
    ...
  </sparql>
```

Note that special column name "?uri" which is used to add the URI of the resource to the created record. Other column names refer to the URIs of RDF properties.

The configuration parameters are:

  * **endpoint**: the URI of the SPARQL endpoint
  * **query**: the SPARQL query to send
  * **page-size**: how many rows to load per query (set to 0 to turn paging off)
  * **triple-mode**: whether to use triple mode or not

Note that the data source will use paging to control the size of query results, so the query must order the query results for paging to work consistently. The default page size is 1000 rows.

Below is the same example reformulated to tabular mode:

```
  <sparql>
    <param name="endpoint" value="http://localhost:8080/sparql/"/>
    <param name="triple-mode" value="false"/>
    <param name="query" value="
      SELECT DISTINCT ?uri ?name ...
      WHERE {
        ?uri a &lt;http://xmlns.com/foaf/0.1/Person> .
        ?uri foaf:name ?name .
      }
      ORDER BY ?uri
    "/>

    <column name="?uri"
            property="ID"/>
    <column name="?name"
            cleaner="no.priv.garshol.duke.LowerCaseNormalizeCleaner"
            property="NAME"/>
    ...
  </sparql>
```

# Custom data sources #

If you want to use a custom data source, simply implement the DataSource interface, and then configure it as follows in the XML file:

```
  <data-source class="class name here">
    <param name="bean property" value="value"/>
    ...
  </data-source>
```