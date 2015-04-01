package org.codejargon.fluentjdbc.api.query;

import java.util.List;

/**
 * @param <T> Type of generated key(s) for a single row inserted
 */
public interface UpdateResultGeneratedKeys<T> extends UpdateResult {
    /**
     * @return generated key(s) for each rows inserted by the statement
     */
    List<T> generatedKeys();
}
