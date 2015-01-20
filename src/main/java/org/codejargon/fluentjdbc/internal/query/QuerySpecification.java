package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedSqlAndParams;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class QuerySpecification {
    final String sql;
    final List<Object> params;
    final Map<String, Object> namedParams;
    final Optional<Integer> selectFetchSize;

    QuerySpecification(String sql, List<Object> params, Map<String, Object> namedParams, Optional<Integer> selectFetchSize) {
        this.sql = sql;
        this.params = params;
        this.namedParams = namedParams;
        this.selectFetchSize = selectFetchSize;
    }

    SqlAndParams sqlAndParams(QueryConfig config) {
        return namedParams.isEmpty() ?
                new SqlAndParams(sql, params) :
                NamedSqlAndParams.sqlAndParams(config.transformedSql(sql), namedParams);
    }
}
