package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.TransformedSql;

import java.util.Map;
import java.util.Optional;

public class QueryConfig {
    final ParamAssigner paramAssigner;
    final Map<String, TransformedSql> namedParamSqlCache;
    final Optional<Integer> defaultFetchSize;

    public QueryConfig(
            ParamAssigner paramAssigner, 
            Map<String, TransformedSql> namedParamSqlCache, 
            Optional<Integer> defaultFetchSize
    ) {
        this.paramAssigner = paramAssigner;
        this.namedParamSqlCache = namedParamSqlCache;
        this.defaultFetchSize = defaultFetchSize;
    }

    TransformedSql transformedSql(String sql) {
        if(!namedParamSqlCache.containsKey(sql)) {
            namedParamSqlCache.put(sql, TransformedSql.forSql(sql));
        }
        return namedParamSqlCache.get(sql);
    }
    
    Optional<Integer> fetchSize(Optional<Integer> selectFetchSize) {
        return selectFetchSize.isPresent() ? selectFetchSize : defaultFetchSize;
    }
}
