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
     * Adds positional query parameters.
     *
     * @param params additional query parameters
     * @return this
     */
    SelectQuery params(List<?> params);

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
     * Sets a custom error handler
     *
     * @param sqlErrorHandler
     * @return this
     */
    SelectQuery errorHandler(SqlErrorHandler sqlErrorHandler);

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
     */
     void iterateResult(SqlConsumer<ResultSet> consumer);

    /**
     * Runs the select query and provides results to the given consumer
     *
     * @param mapper ResultSet mapper
     * @param consumer Consumer accepting the results
     * @param <T> result type
     */
    <T> void iterateResult(Mapper<T> mapper, Consumer<T> consumer);


}
