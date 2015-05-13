package org.codejargon.fluentjdbc.api.query.inspection;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MetaDataResultSet {
    ResultSet select(DatabaseMetaData metaData) throws SQLException;
}
