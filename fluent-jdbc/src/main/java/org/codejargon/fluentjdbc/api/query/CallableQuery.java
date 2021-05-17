package org.codejargon.fluentjdbc.api.query;

import java.sql.CallableStatement;
import java.util.List;
import java.util.Map;

/**
 * Select query for a SQL statement. A SelectQuery is mutable, non-threadsafe.
 */
public interface CallableQuery {
    /**
     * Adds positional query parameters.
     *
     * @param params additional query parameters
     * @return this
     */
    CallableQuery params(List<?> params);

    /**
     * Adds positional query parameters.
     *
     * @param params additional query parameters
     * @return this
     */
    CallableQuery params(Object... params);

    /**
     * Adds named query paramaters.
     *
     * @param namedParams additional named query parameters
     * @return this
     */
    CallableQuery namedParams(Map<String, ?> namedParams);

    /**
     * Adds a named query parameter
     *
     * @param name name of parameter
     * @param parameter value of parameter
     * @return this
     */
    CallableQuery namedParam(String name, Object parameter);

    /**
     * Sets a custom error handler
     *
     * @param sqlErrorHandler
     * @return this
     */
    CallableQuery errorHandler(SqlErrorHandler sqlErrorHandler);

    /**
     * executes the callable statement and returns a single result.
     *
     * @param mapper {@link CallableStatement} mapper
     * @param <T> result type
     * @return exactly one result
     * @throws org.codejargon.fluentjdbc.api.FluentJdbcException if no result found
     */
    <T> T result(CallableMapper<T> mapper);
}
