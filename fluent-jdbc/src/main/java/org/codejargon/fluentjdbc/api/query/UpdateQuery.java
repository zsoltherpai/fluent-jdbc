package org.codejargon.fluentjdbc.api.query;

import java.util.List;
import java.util.Map;

/**
 * Update or insert Query for a SQL statement. An UpdateQuery is mutable.
 */
public interface UpdateQuery {
    /**
     * Adds positional query parameters
     *
     * @param params additional query parameters
     * @return this
     */
    UpdateQuery params(List<?> params);

    /**
     * Adds positional query parameters
     *
     * @param params additional query parameters
     * @return this
     */
    UpdateQuery params(Object... params);

    /**
     * Adds named query paramaters
     *
     * @param namedParams additional named query parameters
     * @return this
     */
    UpdateQuery namedParams(Map<String, ?> namedParams);

    /**
     * Runs the update query
     *
     * @return result of the update (eg affected rows)
     */
    UpdateResult run();

    /**
     * Runs the update query and fetches the generated keys
     *
     * @param generatedKeyMapper maps generated key(s) to an object
     * @param <T> type of a single key or an object containing multiple keys
     * @return result of the update including generated keys
     */
    <T> UpdateResultGenKeys<T> runFetchGenKeys(Mapper<T> generatedKeyMapper);


}
