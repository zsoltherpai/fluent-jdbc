package org.fluentjdbc.api.query;

import java.util.List;
import java.util.Map;

/**
 * Update or insert Query for a SQL statement. An UpdateQuery is mutable, non-threadsafe.
 */
public interface UpdateQuery {
    /**
     * Adds positional query parameters. Can not be added if named parameters are already specified.
     *
     * @param params additional query parameters
     * @return this
     */
    UpdateQuery params(List<Object> params);

    /**
     * Adds positional query parameters. Can not provided if named parameters are already specified.
     *
     * @param params additional query parameters
     * @return this
     */
    UpdateQuery params(Object... params);

    /**
     * Adds named query paramaters. Can not be added if positional parameters are already added.
     *
     * @param namedParams additional named query parameters
     * @return this
     */
    UpdateQuery namedParams(Map<String, Object> namedParams);

    /**
     * Runs the update query and returns the result of it (eg affected rows)
     *
     * @return result of the update (eg affected rows)
     */
    UpdateResult run();
}
