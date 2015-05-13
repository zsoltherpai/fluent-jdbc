package org.codejargon.fluentjdbc.api.query.inspection;

public interface DatabaseInspection {
    <T> T accessDbMetaData(MetaDataAccess<T> access);
    MetaDataSelect selectFromDbMetaData(MetaDataResultSet select);
}
