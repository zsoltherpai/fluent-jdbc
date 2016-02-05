####About FluentJdbc####
[FluentJdbc](http://zsoltherpai.github.io/fluent-jdbc) is a java library for operating with SQL queries conveniently. Every SQL operation is a single,
readable api call without any of the JDBC clutter. It supports functionality most similar
jdbc wrappers prevent or abstract away, more details below.

FluentJdbc's key features:
* functional, fluent API
* execution of select/insert/update/delete/alter/... statements as one-liners
* parameter mapping (named, positional, supports java.time, plugins for custom types)
* accessing generated keys of insert/update queries
* transaction handling
* big data (scalable, streaming style of batch and select)
* automatic result to pojo mapping
* database inspection
* query listener (for eg logging, auditing, performance measurement)

```xml
<dependency>
    <groupId>org.codejargon</groupId>
    <artifactId>fluentjdbc</artifactId>
    <version>1.0.5</version>
</dependency>
```
Note: requires java 8

Full documentation on [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)

Latest [javadoc](https://github.com/zsoltherpai/fluent-jdbc/wiki/Javadoc)

#####News#####
* 1.0.5 released - query listener (for logging, etc), java.util.Date precision bugfix, convenience method for named params

#####Code examples of common use cases#####
######Setting up FluentJdbc######
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(dataSource)
	.build();
Query query = fluentJdbc.query();
// ... use the Query interface for queries (thread-safe, reentrant)
```
Note: using a DataSource is the most common, there are other alternatives documented on the [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)
######Update or insert queries######
```java
query
	.update("UPDATE CUSTOMER SET NAME = ?, ADDRESS = ?")
	.params("John Doe", "Dallas")
	.run();
```
######Query for a list of results######
```java
List<Customer> customers = query
	.select("SELECT * FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.listResult(customerMapper);
```
######Mapping of results######
Mapper<Customer> can be implemented manually
```java
resultSet -> new Customer(resultSet.getString("NAME"), resultSet.getString("ADDRESS"));
```
or mapping can be performed automatically to a java object
```java
ObjectMappers objectMappers = ObjectMappers.builder().build(); //typically one instance per app
...
Mapper<Customer> customerMapper = objectMappers.forClass(Customer.class);
```
######Query for single result######
```java
Long count = query
	.select("SELECT COUNT(*) FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.singleResult(Mappers.singleLong);
```
######Query for first result######
```java
Optional<Customer> customer = query
	.select("SELECT FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.firstResult(customerMapper);
```

######Batch insert or update######
```java
Iterator<List<Object>> params = ...; // or Stream/Iterable
query
	.batch("INSERT INTO CUSTOMER(NAME, ADDRESS) VALUES(?, ?)")
	.params(params)
	.run();
```
######Named parameters######
```java
query
	.batch("UPDATE CUSTOMER SET NAME = :name, ADDRESS = :address")
	.namedParam("name", "John Doe")
	.namedParam("address", "Dallas")
	.run();
```

######java.time support for query parameters######
```java
query
	.update("UPDATE CUSTOMER SET DEADLINE = ?, UPDATED = ?")
	.params(LocalDate.of(2015, Month.MARCH, 5), Instant.now())
	.run();
```
######Iterating a large resultset######
```java
query
	.select("SELECT * FROM CUSTOMER")
	.iterateResult(customerMapper, (customer) -> {
		if(customer.isExpired()) {
			...
		}
	});
```
######Query for a list of limited results######
```java
List<Customer> customers = query
	.select("SELECT * FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.maxRows(345L)
	.listResult(customerMapper);
```
######Fetching generated key of an insert or updates######
```java
UpdateResultGenKeys<Long> result = query
	.update("INSERT INTO CUSTOMER(NAME) VALUES(:name)")
	.namedParams(namedParams)
    .runFetchGenKeys(Mappers.singleLong());
Long id = result.generatedKeys().get(0);
```
######Querying using a specific connection object######
```java
Connection connection = ...
Query query = fluentJdbc.queryOn(connection);
// do some querying...
```
######Transactions######
```java
query.transaction().in(
	() -> {
		query
        	.update("UPDATE CUSTOMER SET NAME = ?, ADDRESS = ?")
        	.params("John Doe", "Dallas")
        	.run();
		someOtherBusinessOperationAlsoNeedingTransactions();
	}
)
```
All queries executed in the block will be part of the transaction - in the same thread, based on the same FluentJdbc/ConnectionProvider.
Exceptions cause rollback. It is possible to use multiple transactions/datasources simultaneously.
######Query listener######
A listener provides a callback mechanism called on each FluentJdbc query operation. This allows things like SQL statement logging,
performance measurement. The following example logs all successful SQL operations along with the time taken to execute them:
```java
AfterQueryListener listener = execution -> {
    if(execution.success()) {
        log.debug(
            String.format(
                "Query took %s ms to execute: %s",
                execution.executionTimeMs(),
                execution.sql()
            )
        )
    }
};

FluentJdbc fluentJdbc = new FluentJdbcBuilder()
    // other configuration
    .afterQueryListener(listener)
    .build();

// run queries
```

Refer to the [full documentation](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation) for more details and code examples.