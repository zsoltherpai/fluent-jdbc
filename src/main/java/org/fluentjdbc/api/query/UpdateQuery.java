package org.fluentjdbc.api.query;

import java.util.List;

/**
 * Update or insert Query for a SQL statement. An UpdateQuery is mutable, non-threadsafe.
 */
public interface UpdateQuery {
    /**
     * Adds query parameters
     *
     * @param params query parameters
     * @return this
     */
    UpdateQuery params(List<Object> params);

    /**
     * Adds query parameters
     *
     * @param params query parameters
     * @return this
     */
    UpdateQuery params(Object... params);

    /**
     * Runs the update query and returns the result of it (eg affected rows)
     *
     * @return result of the update (eg affected rows)
     */
    UpdateResult run();
}
