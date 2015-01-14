package org.fluentjdbc.api.query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Select query for a SQL statement. A SelectQuery is mutable, non-threadsafe.
 */
public interface SelectQuery {
    /**
     * Adds query parameters
     *
     * @param params additional query parameters
     * @return this
     */
    SelectQuery params(List<Object> params);

    /**
     * Adds query parameters
     *
     * @param params additional query parameters
     * @return this
     */
    SelectQuery params(Object... params);


    /**
     * Sets a result filter. Only results accepted by this Predicate will be returned by SelectQuery.
     *
     * @param predicate filter
     * @param <T> result type
     * @return this
     */
    <T> SelectQuery filter(Predicate<T> predicate);


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
     * @throws org.fluentjdbc.api.FluentJdbcException if no result found
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
     * Runs the select query and provides results to the given consumer
     *
     * @param mapper ResultSet mapper
     * @param consumer Consumer accepting the results
     * @param <T> result type
     */
    <T> void iterateResult(Mapper<T> mapper, Consumer<T> consumer);
}
