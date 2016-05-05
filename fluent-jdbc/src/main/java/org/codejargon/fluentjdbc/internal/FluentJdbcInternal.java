package org.codejargon.fluentjdbc.internal;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.query.QueryConfig;
import org.codejargon.fluentjdbc.internal.query.QueryInternal;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

public class FluentJdbcInternal implements FluentJdbc {

    private final Optional<ConnectionProvider> connectionProvider;
    private final QueryConfig queryConfig;

    public FluentJdbcInternal(
            Optional<ConnectionProvider> connectionProvider, 
            Map<Class, ParamSetter> paramSetters,
            Optional<Integer> defaultFetchSize,
            Optional<AfterQueryListener> afterQueryListener
    ) {
        this.connectionProvider = connectionProvider;
        queryConfig = new QueryConfig(defaultFetchSize, paramSetters, afterQueryListener);
    }

    @Override
    public Query query() {
        if (!connectionProvider.isPresent()) {
            throw new FluentJdbcException("ConnectionProvider is not set.");
        }
        return new QueryInternal(connectionProvider.get(), queryConfig);
    }



    @Override
    public Query queryOn(Connection connection) {
        return new QueryInternal(
                query -> query.receive(connection), 
                queryConfig
        );
    }
}
