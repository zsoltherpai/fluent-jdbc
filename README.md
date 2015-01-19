####About FluentJdbc####

FluentJdbc provides a fluent API for executing SQL queries in java. It is best suited for projects that
require fine control over SQL queries and operations in a convenient, declarative way.

It wraps JDBC, provides additional features while avoids JDBC inconveniences and clutter. FluentJdbc is 
light-weight, has no 3rd party dependencies.

#####Main advantages over plain JDBC#####
- a flexible, functional API making the most common JDBC operations trivial one-liners
- implicit resource management, avoiding leaks of Connections, PreparedStatements, ResultSets
- out of the box support for java.time, Extension API for more custom types
- automatic mapping of results to POJOs
- named query parameters

```
<dependency>
    <groupId>org.codejargon</groupId>
    <artifactId>fluentjdbc</artifactId>
    <version>0.8</version>
</dependency>
```
Note: requires java 8

Full documentation on [wiki](https://github.com/zsoltherpai/fluent-jdbc/wiki)  
Latest [javadoc](http://zsoltherpai.github.io/fluent-jdbc/apidocs-0.8)

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
query
	.select("SELECT * FROM CUSTOMER")
	.iterateResult(customerMapper, (customer) -> {
		if(customer.isExpired()) {
			...
		}
	});

######Creating FluentJdbc with DataSource as connection provider######
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(new DataSourceConnectionProvider(dataSource)
        .build();
Query query = fluentJdbc.query();
```
######Querying with a specific connection object######
```java
Connection connection = ...
Query query = fluentJdbc.queryOn(connection)...
```
######Creating a custom connection provider - eg spring JdbcOperations/JdbcTemplate######
```java
ConnectionProvider provider = query -> {
	jdbcOperations.execute(connection -> {
		query.receive(connection);
   	});
}
```
