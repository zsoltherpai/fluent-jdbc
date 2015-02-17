package org.codejargon.fluentjdbc.internal;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.internal.query.QueryConfig;
import org.codejargon.fluentjdbc.internal.query.QueryInternal;

public class FluentJdbcInternal implements FluentJdbc {

    private final Optional<ConnectionProvider> connectionProvider;
    private final QueryConfig queryConfig;

    public FluentJdbcInternal(
            Optional<ConnectionProvider> connectionProvider, 
            Map<Class, ParamSetter> paramSetters,
            Optional<Integer> defaultFetchSize
    ) {
        this.connectionProvider = connectionProvider;
        queryConfig = new QueryConfig(defaultFetchSize, paramSetters);
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
