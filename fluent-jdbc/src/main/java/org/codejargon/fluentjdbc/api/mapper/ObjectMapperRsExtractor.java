package org.codejargon.fluentjdbc.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Plug-in for supporting custom types in ObjectMappers</p>
 * @see org.codejargon.fluentjdbc.api.mapper.ObjectMappers
 */
@FunctionalInterface
public interface ObjectMapperRsExtractor<T> {
    /**
     * Extracts an object from a ResultSet and converts it to the object of target class.
     * @param resultSet ResultSet containing the current row.
     * @param index column index in the ResultSet
     * @return Object of the required class - nullable
     * @throws SQLException
     */
    T extract(ResultSet resultSet, Integer index) throws SQLException;
}
