####About FluentJdbc####

FluentJdbc provides a fluent API for executing JDBC SQL queries in Java 8. The main focus is on querying convenience 
and easy integration with most connection pooling and transaction managing solutions.

The solution is lightweight and has no external dependencies.

#####Main advantages over plain JDBC#####
- a flexible, functional API making the most common JDBC operations trivial one-liners
- implicit resource management, avoiding leaks of Connections, PreparedStatements, ResultSets
- out of the box support for non-jdbc data types (java.time), plugin API for custom types
- automatic mapping of results to java beans, plugin API for custom types
- named query parameters

Full documentation on wiki:

#####Code examples#####

######Update or insert queries######
```java
fluentJdbc.query()
	.update("UPDATE FOO SET BAR1 = ?, BAR2 = ?")
	.params("myParam1", "myParam2")
	.run();
```
######Query for a list of results######
```java
List<Foo> foo = fluentJdbc.query()
	.select("SELECT * FROM FOO WHERE BAR = ?")
	.params("myParam")
	.listResult(fooMapper); 
```
######Mapping of results######
Mapper can be done manually
```java
resultSet -> new Foo(resultSet.getString("BAR"))
```
or automatically to a java bean
```java
objectMappers.forClass(Foo.class);
```
######Query for single result######
```java
Long count = fluentJdbc.query()
	.select("SELECT COUNT(*) FROM FOO WHERE BAR = ?")
	.params("myParam")
	.singleResult(Mappers.singleLong);
```
######Batch insert or update######
```java
Iterator<List<Object>> params = ...;
fluentJdbc.query()
	.batch("INSERT INTO FOO(BAR1, BAR2) VALUES(?, ?)")
	.params(params)
	.singleResult(Mappers.singleLong);
```
######Named parameters######
Map<String, Object> namedParams = new HashMap<>();
namedParams.put("myParam1", "paramValue1");
namedParams.put("myParam2", "paramValue2");

fluentJdbc.query()
	.update("UPDATE FOO SET BAR1 = :myParam1, BAR2 = :myParam2")
	.namedParams(namedParams)
	.run();

######java.time support for query parameters######
```java
fluentJdbc.query()
	.update("UPDATE FOO SET MYDATE = ?, UPDATED = ?")
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
