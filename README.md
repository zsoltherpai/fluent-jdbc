####About FluentJdbc####
FluentJdbc provides a functional, fluent API for executing native SQL queries without any of the JDBC boiler-plate. Every
operation is a single, well-readable statement.

Some of the features:
* easy integration to a project (through DataSource or other alternatives)
* execution of select/insert/update/delete/alter/... statements as one-liners
* parameter mapping (named, positional, supporting java.time, possibility to extend with custom types)
* accessing generated keys of insert/update queries
* automatic result -> pojo mapping
* transaction handling

```xml
<dependency>
    <groupId>org.codejargon</groupId>
    <artifactId>fluentjdbc</artifactId>
    <version>0.9.9</version>
</dependency>
```
Note: requires java 8

Full documentation on [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)

Latest [javadoc](https://github.com/zsoltherpai/fluent-jdbc/wiki/Javadoc)

#####News#####
* 0.9.9 released - note that license has been changed from MIT to Apache 2.0

#####Code examples#####
Some common use cases

######Setting up FluentJdbc######
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(new DataSourceConnectionProvider(dataSource))
	.build();
Query query = fluentJdbc.query();
// ... use the Query interface for queries (thread-safe, reentrant)
```
Note: using a DataSource is the most common, there are alternatives shown on the [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation)
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
ObjectMappers objectMappers = ObjectMappers.builder().build(); //generally one instance per app
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
Iterator<List<Object>> params = ...;
query
	.batch("INSERT INTO CUSTOMER(NAME, ADDRESS) VALUES(?, ?)")
	.params(params)
	.run();
```
######Named parameters######
```java
Map<String, Object> namedParams = new HashMap<>();
namedParams.put("name", "John Doe");
namedParams.put("address", "Dallas");

query
	.batch("UPDATE CUSTOMER SET NAME = :name, ADDRESS = :address")
	.namedParams(namedParams)
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
FluentJdbc fluentJdbc = ...
Connection connection = ...
Query query = fluentJdbc.queryOn(connection);
...
```
######Transactions######
```java
query.transaction().in(
	() -> {
		query
        	.update("UPDATE CUSTOMER SET NAME = ?, ADDRESS = ?")
        	.params("John Doe", "Dallas")
        	.run();
		someOtherMethodAlsoDoingQueriesOrTransactions();
	}
)
```
All queries executed in the block will be part of the transaction - in the same thread, based on the same FluentJdbc/ConnectionProvider.
Exceptions cause rollback. It is possible to use multiple DataSources/transactions simultaneously.

Refer to the [full documentation](https://github.com/zsoltherpai/fluent-jdbc/wiki/Motivation) for more details and code examples.