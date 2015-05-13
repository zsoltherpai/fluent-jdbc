package org.codejargon.fluentjdbc.api.query.inspection;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@FunctionalInterface
public interface MetaDataResultSet {
    ResultSet select(DatabaseMetaData metaData);
}
