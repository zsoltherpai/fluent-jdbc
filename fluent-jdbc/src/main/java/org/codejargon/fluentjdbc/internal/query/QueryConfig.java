package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSqlFactory;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.Map;
import java.util.Optional;

public class QueryConfig {
    final ParamAssigner paramAssigner;

    final Optional<Integer> defaultFetchSize;
    final AfterQueryListener afterQueryListener;
    private final NamedTransformedSqlFactory namedTransformedSqlFactory;

    public QueryConfig(
            Optional<Integer> defaultFetchSize,
            Map<Class, ParamSetter> paramSetters,
            AfterQueryListener afterQueryListener
    ) {
        this.paramAssigner = new ParamAssigner(
                Maps.merge(DefaultParamSetters.setters(), paramSetters)
        );
        this.namedTransformedSqlFactory = new NamedTransformedSqlFactory();
        this.defaultFetchSize = defaultFetchSize;
        this.afterQueryListener = afterQueryListener;
    }

    NamedTransformedSql namedTransformedSql(String sql, Map<String, ?> namedParams) {
        return namedTransformedSqlFactory.namedTransformedSqlWithPossibleConnections(sql, namedParams);
    }
    
    Optional<Integer> fetchSize(Optional<Integer> selectFetchSize) {
        return selectFetchSize.isPresent() ? selectFetchSize : defaultFetchSize;
    }
}
