#### About FluentJdbc
[FluentJdbc](http://zsoltherpai.github.io/fluent-jdbc) is a java library for convenient native SQL querying. Blends well with Java 8 / functional code, 
supports functionality many jdbc wrappers prevent / abstract away, and is lightweight (~80K, no dependencies).

FluentJdbc's key features:
* functional, fluent API
* execution of select/insert/update/delete/alter/... statements as one-liners
* parameter mapping (named, positional, supports java.time, enums, Optional, Collections, plugins for custom types)
* transactions
* access to generated keys of insert/update queries
* big data (scalable, streaming style of batch and select)
* automatic result to pojo mapping
* database inspection
* query listener (for logging, auditing, performance measurement, ...)

```xml
<dependency>
    <groupId>org.codejargon</groupId>
    <artifactId>fluentjdbc</artifactId>
    <version>1.8.3</version>
</dependency>
```
Note: requires java 8

Full documentation on [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)

Latest [javadoc](https://github.com/zsoltherpai/fluent-jdbc/wiki/Javadoc)

##### Code examples of common use cases
###### Setting up FluentJdbc
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(dataSource)
	.build();
Query query = fluentJdbc.query();
// ... use the Query interface for queries (thread-safe, reentrant)
```
Note: using a DataSource is the most common scenario, there are other alternatives documented on the [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)
###### Update or insert queries
```java
query
	.update("UPDATE CUSTOMER SET NAME = ?, ADDRESS = ?")
	.params("John Doe", "Dallas")
	.run();
```
###### Query for a list of results
```java
List<Customer> customers = query.select("SELECT * FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.listResult(customerMapper);
```
###### Mapping of results
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
###### Query for single result
```java
Long count = query.select("SELECT COUNT(*) FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.singleResult(Mappers.singleLong);
```
###### Query for first result
```java
Optional<Customer> customer = query.select("SELECT FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.firstResult(customerMapper);
```

###### Batch insert or update
```java
Stream<List<Object>> params = ...; // or Iterator/Iterable
query.batch("INSERT INTO CUSTOMER(NAME, ADDRESS) VALUES(?, ?)")
	.params(params)
	.run();
```
###### Named parameters
```java
query.update("UPDATE CUSTOMER SET NAME = :name, ADDRESS = :address")
	.namedParam("name", "John Doe")
	.namedParam("address", "Dallas")
	.run();
```
Note: or .namedParams(mapOfParams)
###### java.time support for query parameters
```java
query.update("UPDATE CUSTOMER SET DEADLINE = ?, UPDATED = ?")
	.params(LocalDate.of(2015, Month.MARCH, 5), Instant.now())
	.run();
```
Note: support for any type can be implemented
###### java.util.Optional support
```java
Optional<LocalData> deadline = ...
query.update("UPDATE CUSTOMER SET DEADLINE = ?")
	.params(deadline)
	.run();
```
###### Collection parameter support
```java
Set<Long> ids = ...
List<Customer> customers = query.select("SELECT * FROM CUSTOMER WHERE ID IN (:ids)")
	.namedParam("ids", ids)
	.listResult(customerMapper);;
```
Note: supported for named parameters
###### Iterating a large resultset
```java
query.select("SELECT * FROM CUSTOMER")
	.iterateResult(rs -> {
		// do something with the row
	});
```
###### Query for a list of limited results
```java
List<Customer> customers = query.select("SELECT * FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.maxRows(345L)
	.listResult(customerMapper);
```
###### Fetching generated key of an insert or updates
```java
UpdateResultGenKeys<Long> result = query
	.update("INSERT INTO CUSTOMER(NAME) VALUES(:name)")
	.namedParams(namedParams)
    .runFetchGenKeys(Mappers.singleLong());
Long id = result.generatedKeys().get(0);
```
###### Querying using a specific connection object
```java
Connection connection = ...
Query query = fluentJdbc.queryOn(connection);
// do some querying...
```
###### Transactions
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
###### Query listener
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