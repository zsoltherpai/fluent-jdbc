####About FluentJdbc####
FluentJdbc provides a fluent API for executing native SQL queries. It is best suited for projects that
require fine control over SQL queries and operations in a convenient, declarative way. Can be used
standalone or complement higher level of abstractions like JPA or others.

It provides features like: support for custom parameter types (like java.time), named query parameters, 
automatic ResultSet to POJO mapping, etc. It also avoids inconveniences of JDBC API, like: 
checked exceptions, explicit resource management / leaks, clutter. FluentJdbc is light-weight, 
has no 3rd party dependencies.

#####Main advantages over plain JDBC#####
- a flexible, functional API making the most common JDBC operations trivial one-liners
- implicit resource management, avoiding leaks of Connections, PreparedStatements, ResultSets
- out of the box support for java.time, extension API for more custom types
- automatic mapping of results to POJOs
- named query parameters

```xml
<dependency>
    <groupId>org.codejargon</groupId>
    <artifactId>fluentjdbc</artifactId>
    <version>0.9.2</version>
</dependency>
```
Note: requires java 8

Full documentation on [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki)  
Latest [javadoc](https://github.com/zsoltherpai/fluent-jdbc/wiki/Javadoc)

#####News#####
* 0.9.2 released - no-arg constructor of ObjectMappers POJOs no longer needs to be public, performance tweaks

#####Code examples#####
Some common use cases

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
or mapping can be performed automatically to a java bean
```java
objectMappers.forClass(Customer.class);
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

######Creating FluentJdbc with DataSource as connection provider######
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(new DataSourceConnectionProvider(dataSource))
        .build();
Query query = fluentJdbc.query();
```
######Querying with a specific connection object######
```java
Connection connection = ...
Query query = fluentJdbc.queryOn(connection)...
```
######Creating a custom connection provider - eg spring JdbcTemplate######
```java
ConnectionProvider provider = query -> {
	jdbcTemplate.execute(connection -> {
		query.receive(connection);
   	});
}
```
######Transactions######
Transaction-managed connections can be provided by the ConnectionProvider implementation. This can be done in a number of ways, eg:
- Using a transaction-aware DataSource (eg JEE DataSources, Spring's TransactionAwareDataSourceProxy)
- Extracting the underlying connection from a JPA session (eg Guice Persist with JPA)
- Getting transaction-managed connection from a Connection callback.
- There is an extension library for Guice Persist: fluentjdbc-guice-persist, which supports standalone transaction management (without JPA or other tech)
