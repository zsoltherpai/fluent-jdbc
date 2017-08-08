package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;
import org.codejargon.fluentjdbc.api.query.Transaction;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSqlFactory;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class QueryConfig {
    final ParamAssigner paramAssigner;

    private final Optional<Integer> defaultFetchSize;
    final Optional<Integer> defaultBatchSize;
    final Optional<AfterQueryListener> afterQueryListener;
    final NamedTransformedSqlFactory namedTransformedSqlFactory;
    final Optional<Transaction.Isolation> defaultTransactionIsolation;
    final Supplier<SqlErrorHandler> defaultSqlErrorHandler;

    public QueryConfig(
            Optional<Integer> defaultFetchSize,
            Optional<Integer> defaultBatchSize,
            Map<Class, ParamSetter> paramSetters,
            Optional<AfterQueryListener> afterQueryListener,
            Optional<Transaction.Isolation> defaultTransactionIsolation,
            Supplier<SqlErrorHandler> defaultSqlErrorHandler
    ) {
        this.paramAssigner = new ParamAssigner(
                Maps.merge(DefaultParamSetters.setters(), paramSetters)
        );
        this.namedTransformedSqlFactory = new NamedTransformedSqlFactory();
        this.defaultFetchSize = defaultFetchSize;
        this.defaultBatchSize = defaultBatchSize;
        this.afterQueryListener = afterQueryListener;
        this.defaultTransactionIsolation = defaultTransactionIsolation;
        this.defaultSqlErrorHandler = defaultSqlErrorHandler;
    }

    
    Optional<Integer> fetchSize(Optional<Integer> selectFetchSize) {
        return selectFetchSize.isPresent() ? selectFetchSize : defaultFetchSize;
    }
}
