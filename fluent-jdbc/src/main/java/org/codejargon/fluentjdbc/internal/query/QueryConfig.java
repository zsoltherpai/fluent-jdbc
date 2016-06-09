package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSqlFactory;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.Map;
import java.util.Optional;

public class QueryConfig {
    final ParamAssigner paramAssigner;

    private final Optional<Integer> defaultFetchSize;
    final Optional<AfterQueryListener> afterQueryListener;
    final NamedTransformedSqlFactory namedTransformedSqlFactory;

    public QueryConfig(
            Optional<Integer> defaultFetchSize,
            Map<Class, ParamSetter> paramSetters,
            Optional<AfterQueryListener> afterQueryListener
    ) {
        this.paramAssigner = new ParamAssigner(
                Maps.merge(DefaultParamSetters.setters(), paramSetters)
        );
        this.namedTransformedSqlFactory = new NamedTransformedSqlFactory();
        this.defaultFetchSize = defaultFetchSize;
        this.afterQueryListener = afterQueryListener;
    }

    
    Optional<Integer> fetchSize(Optional<Integer> selectFetchSize) {
        return selectFetchSize.isPresent() ? selectFetchSize : defaultFetchSize;
    }
}
