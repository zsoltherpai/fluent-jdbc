package org.codejargon.fluentjdbc.api.query;

import java.util.List;
import java.util.Optional;

/**
 * Result of an update / insert including generated keys
 * @param <T> Type of generated key(s) for a single row inserted
 */
public interface UpdateResultGenKeys<T> extends UpdateResult {
    /**
     * @return generated key(s) for each row inserted by the statement
     */
    List<T> generatedKeys();
    Optional<T> firstKey();
}
