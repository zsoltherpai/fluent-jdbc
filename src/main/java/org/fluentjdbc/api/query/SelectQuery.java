package org.fluentjdbc.api.query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface SelectQuery {
    /**
     * Adds query parameters
     */
    SelectQuery params(List<Object> params);
    /**
     * Adds query parameters
     */
    SelectQuery params(Object... params);

    /**
     * Filtering results of the sql query
     */
    <T> SelectQuery filter(Predicate<T> predicate);

    /**
     * Runs the select query and returns first result - if any
     */
    <T> Optional<T> firstResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns first result. Throws FluentJdbcException if no result found
     */
    <T> T singleResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns results as an immutable list
     */
    <T> List<T> listResult(Mapper<T> mapper);

    /**
     * Runs the select query and returns results as an immutable set
     */
    <T> Set<T> setResult(Mapper<T> mapper);

    /**
     * Runs the select query and provides results to the given consumer
     */
    <T> void iterateResult(Mapper<T> mapper, Consumer<T> consumer);
}
