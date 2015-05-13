package org.codejargon.fluentjdbc.api.query.inspection;

/**
 * Inspection of the Database schema
 */
public interface DatabaseInspection {
    /**
     * Provides access to a DatabaseMetaData instance
     * @see java.sql.DatabaseMetaData
     *
     * @param access Callback with access to DatabaseMetaData
     * @param <T> Return object type
     * @return Information extracted from DatabaseMetaData
     */
    <T> T accessMetaData(MetaDataAccess<T> access);

    /**
     * Selects from DatabaseMetaData - for methods returning a ResultSet
     * @see java.sql.DatabaseMetaData
     * @see java.sql.ResultSet
     *
     * @param select Function calling a method on DatabaseMetaData that returns ResultSet
     * @return MetaData select
     */
    MetaDataSelect selectFromMetaData(MetaDataResultSet select);
}
