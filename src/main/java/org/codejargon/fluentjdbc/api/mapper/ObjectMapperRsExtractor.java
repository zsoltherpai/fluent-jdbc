package org.codejargon.fluentjdbc.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * API for supporting custom types in ObjectMapper
 */
public interface ObjectMapperRsExtractor<T> {
    /**
     * Extracts an object from a ResultSet and converts it to the proper class.
     * @param resultSet ResultSet containing the current row.
     * @param index column index in the ResultSet
     * @return Object of the required class - nullable
     * @throws SQLException
     */
    T extract(ResultSet resultSet, Integer index) throws SQLException;
}
