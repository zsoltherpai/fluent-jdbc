package org.codejargon.fluentjdbc.api.query.inspection;

import org.codejargon.fluentjdbc.api.query.Mapper;

import java.util.List;

public interface MetaDataSelect {
    /**
     * Provides results as a list
     *
     * @param mapper ResultSet mapper
     * @param <T> Target object type
     * @return List of results
     */
    <T> List<T> listResult(Mapper<T> mapper);
}
