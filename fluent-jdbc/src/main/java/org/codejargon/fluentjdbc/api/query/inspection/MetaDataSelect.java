package org.codejargon.fluentjdbc.api.query.inspection;

import org.codejargon.fluentjdbc.api.query.Mapper;

import java.util.List;

public interface MetaDataSelect {
    <T> List<T> listResult(Mapper<T> mapper);
}
