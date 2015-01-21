package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedSqlAndParams;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class SingleQuerySpecification {
    final String sql;
    final List<Object> params;
    final Map<String, Object> namedParams;
    final Optional<Select> select;

    private SingleQuerySpecification (
            String sql, 
            List<Object> params, 
            Map<String, Object> namedParams, 
            Optional<Select> select
    ) {
        this.sql = sql;
        this.params = params;
        this.namedParams = namedParams;
        this.select = select;
    }

    static SingleQuerySpecification forSelect(
            String sql,
            List<Object> params,
            Map<String, Object> namedParams,
            Optional<Integer> selectFetchSize,
            Optional<Long> maxRows
    ) {
        return new SingleQuerySpecification(
                sql, 
                params, 
                namedParams, 
                Optional.of(new Select(selectFetchSize, maxRows))
        );
    }

    static SingleQuerySpecification forUpdate(
            String sql,
            List<Object> params,
            Map<String, Object> namedParams
    ) {
        return new SingleQuerySpecification(
                sql, 
                params, 
                namedParams, 
                Optional.<Select>empty()
        );
    }

    SqlAndParams sqlAndParams(QueryConfig config) {
        return namedParams.isEmpty() ?
                new SqlAndParams(sql, params) :
                NamedSqlAndParams.sqlAndParams(config.transformedSql(sql), namedParams);
    }
    
    static class Select {
        final Optional<Integer> fetchSize;
        final Optional<Long> maxRows;

        Select(Optional<Integer> fetchSize, Optional<Long> maxRows) {
            this.fetchSize = fetchSize;
            this.maxRows = maxRows;
        }
    }
}
