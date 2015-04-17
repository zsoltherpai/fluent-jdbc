package org.codejargon.fluentjdbc.api.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row in a ResultSet to an object
 * @param <T> target class
 */
@FunctionalInterface
public interface Mapper<T> {
    /**
     *
     * @param rs ResultSet containing data of a table row. ResultSet should not be mutated in a Mapper.
     * @return result object
     * @throws SQLException a Mapper is allowed to throw SQLException for implementation convenience - thrown by most ResultSet methods
     */
    T map(ResultSet rs) throws SQLException;
}
