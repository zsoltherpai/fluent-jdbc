package org.fluentjdbc.api.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    /**
     *
     * @param rs ResultSet containing data of a table row. ResultSet not be mutated in a Mapper.
     * @return result object
     * @throws SQLException a Mapper is allowed to throw SQLException for implementation convenience - thrown by most ResultSet methods
     */
    T map(ResultSet rs) throws SQLException;
}
