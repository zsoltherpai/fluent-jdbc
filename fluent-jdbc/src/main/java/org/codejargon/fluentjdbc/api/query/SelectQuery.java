package org.codejargon.fluentjdbc.api.query;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Select query for a SQL statement. A SelectQuery is mutable, non-threadsafe.
 */
public interface SelectQuery {
    /**
     * Adds a single query parameter.
     *
     * @param param one additional query parameter
     * @return this
     */
    SelectQuery param(Object param);
    
    /**
     * Adds positional query parameters.
     *
     * @param params additional query parameters
     * @return this
     */
    SelectQuery params(List<?> params);
    
    /**
     * Adds two positional query parameters.
     * 
     * @param param1 first additional query parameter
     * @param param2 second additional query parameter
     * @return this
     */
    SelectQuery params(Object param1, Object param2);
    
    /**
     * Adds positional query parameters.
     *
     * @param params additional query parameters
     * @return this
     */
    SelectQuery params(Object... params);

    /**
     * Adds named query paramaters.
     *
     * @param namedParams additional named query parameters
     * @return this
     */
    SelectQuery namedParams(Map<String, ?> namedParams);

    /**
     * Adds a named query parameter
     *
     * @param name name of parameter
     * @param parameter value of parameter
     * @return this
     */
    SelectQuery namedParam(String name, Object parameter);


    /**
     * Sets a result filter. Only results accepted by this Predicate will be returned by SelectQuery.
     *
     * @param predicate filter
     * @param <T> result type
     * @return this
     */
    <T> SelectQuery filter(Predicate<T> predicate);

    /**
     * Sets fetch size of select statements - the number of rows returned in a single network round-trip.
     * FluentJdbc configured default or vendor default is used if not set. Note that vendor defaults 
     * may be different. Eg MySQL default is 0 (no limit) which may lead to memory issues, Oracle DB's default
     * is 10 which may result in poor performance with large ResultSets.
     * @param rows Number of rows fetched by a select statement.
     * @return this
     */
    SelectQuery fetchSize(Integer rows);

    /**
     * Limits the number of rows returned by the database for a select statement. 
     * If rows &lt; Integer.MAX_VALUE, JDBC driver must support setLargeMaxRows()
     * @param rows number of rows
     * @return this
     */
    SelectQuery maxRows(Long rows);

    /**
     * Runs the select query and returns first result - if any
     *
     * @param mapper ResultSet mapper
     * @param <T> result type
     * @return Optional of the result (if there is one)
     */
    <T> Optional<T> firstResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns a single result.
     *
     * @param mapper ResultSet mapper
     * @param <T> result type
     * @return exactly one result
     * @throws org.codejargon.fluentjdbc.api.FluentJdbcException if no result found
     */
    <T> T singleResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns results as an immutable list
     *
     * @param mapper ResultSet mapper
     * @param <T> result type
     * @return immutable List of results
     */
    <T> List<T> listResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns results as an immutable set
     *
     * @param mapper ResultSet mapper
     * @param <T> result type
     * @return immutable Set of results
     */
    <T> Set<T> setResult(Mapper<T> mapper);

    /**
     * Runs the select query and provides resultset to the given consumer
     *
     * @param consumer Consumer accepting the ResultSet
     * @param <T> result type
     */
    <T> void iterateResult(SqlConsumer<ResultSet> consumer);

    /**
     * Runs the select query and provides results to the given consumer
     *
     * @param mapper ResultSet mapper
     * @param consumer Consumer accepting the results
     * @param <T> result type
     */
    <T> void iterateResult(Mapper<T> mapper, Consumer<T> consumer);


}
