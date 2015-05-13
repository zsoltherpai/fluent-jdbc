package org.codejargon.fluentjdbc.api.query.inspection;

import java.sql.DatabaseMetaData;

@FunctionalInterface
public interface MetaDataAccess<T> {
    T access(DatabaseMetaData meta);
}
