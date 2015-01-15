####About FluentJdbc####

FluentJdbc provides a fluent API for executing JDBC SQL queries in Java 8. The main focus is on querying convenience 
and easy integration with most connection pooling and transaction managing solutions.

The solution is lightweight and has no external dependencies.

#####Main advantages over plain JDBC#####
- a flexible, functional API making the most common JDBC operations trivial one-liners
- implicit resource management, avoiding leaks of Connections, PreparedStatements, ResultSets
- out of the box support for java.time, Extension API for more custom types
- automatic mapping of results to java beans
- named query parameters

Full documentation on wiki:

#####Code examples#####

######Update or insert queries######
```java
fluentJdbc.query()
	.update("UPDATE CUSTOMER SET NAME = ?, ADDRESS = ?")
	.params("John Doe", "Dallas")
	.run();
```
######Query for a list of results######
```java
List<Customer> foo = fluentJdbc.query()
	.select("SELECT * FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.listResult(customerMapper);
```
######Mapping of results######
Mapper can be done manually
```java
resultSet -> new Customer(resultSet.getString("NAME"), resultSet.getString("ADDRESS"));
```
or automatically to a java bean
```java
objectMapperFactory.create(Customer.class);
```
######Query for single result######
```java
Long count = fluentJdbc.query()
	.select("SELECT COUNT(*) FROM CUSTOMER WHERE NAME = ?")
	.params("John Doe")
	.singleResult(Mappers.singleLong);
```
######Batch insert or update######
```java
Iterator<List<Object>> params = ...;
fluentJdbc.query()
	.batch("INSERT INTO CUSTOMER(NAME, ADDRESS) VALUES(?, ?)")
	.params(params)
	.singleResult(Mappers.singleLong);
```
######Named parameters######
```java
Map<String, Object> namedParams = new HashMap<>();
namedParams.put("name", "John Doe");
namedParams.put("address", "Dallas");

fluentJdbc.query()
	.update("UPDATE CUSTOMER SET NAME = :name, ADDRESS = :address")
	.namedParams(namedParams)
	.run();
```

######java.time support for query parameters######
```java
fluentJdbc.query()
	.update("UPDATE CUSTOMER SET DEADLINE = ?, UPDATED = ?")
	.params(LocalDate.of(2015, Month.MARCH, 5), Instant.now())
	.run();
```
######Creating FluentJdbc with DataSource as connection provider######
```java
DataSource dataSource = ...
FluentJdbc fluentJdbc = new FluentJdbcBuilder()
	.connectionProvider(new DataSourceConnectionProvider(dataSource)
        .build();
```
######Querying with a specific connection object######
```java
Connection connection = ...
fluentJdbc.queryOn(connection)...
```
######Creating a custom connection provider - eg spring JdbcOperations/JdbcTemplate######
```java
ConnectionProvider provider = query -> {
	jdbcOperations.execute(connection -> {
		query.receive(connection);
   	});
}
```
