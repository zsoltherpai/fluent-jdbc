package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class QueryConfig {
    final ParamAssigner paramAssigner;
    final Map<String, NamedTransformedSql> namedParamSqlCache;
    final Optional<Integer> defaultFetchSize;
    final AfterQueryListener afterQueryListener;

    public QueryConfig(
            Optional<Integer> defaultFetchSize,
            Map<Class, ParamSetter> paramSetters,
            AfterQueryListener afterQueryListener
    ) {
        this.paramAssigner = new ParamAssigner(
                Maps.merge(DefaultParamSetters.setters(), paramSetters)
        );
        this.namedParamSqlCache = new ConcurrentHashMap<>();
        this.defaultFetchSize = defaultFetchSize;
        this.afterQueryListener = afterQueryListener;
    }

    NamedTransformedSql namedTransformedSql(String sql) {
        if(!namedParamSqlCache.containsKey(sql)) {
            namedParamSqlCache.put(sql, NamedTransformedSql.forSql(sql));
        }
        return namedParamSqlCache.get(sql);
    }
    
    Optional<Integer> fetchSize(Optional<Integer> selectFetchSize) {
        return selectFetchSize.isPresent() ? selectFetchSize : defaultFetchSize;
    }
}
