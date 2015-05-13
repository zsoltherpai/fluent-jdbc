package org.codejargon.fluentjdbc.api.query.inspection;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@FunctionalInterface
public interface MetaDataAccess<T> {
    T access(DatabaseMetaData meta) throws SQLException;
}
