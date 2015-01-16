package org.codejargon.fluentjdbc.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * API for supporting custom types in ObjectMapper
 */
public interface ObjectMapperRsExtractor<T> {
    T extract(ResultSet resultset, Integer index) throws SQLException;
}
