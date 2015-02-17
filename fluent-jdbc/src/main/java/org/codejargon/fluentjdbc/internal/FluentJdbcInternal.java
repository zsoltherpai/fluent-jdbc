package org.codejargon.fluentjdbc.internal;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.internal.query.ParamAssigner;
import org.codejargon.fluentjdbc.internal.query.QueryConfig;
import org.codejargon.fluentjdbc.internal.query.QueryInternal;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;
import org.codejargon.fluentjdbc.internal.support.Maps;

public class FluentJdbcInternal implements FluentJdbc {

    private final Optional<ConnectionProvider> connectionProvider;
    private final QueryConfig queryConfig;

    public FluentJdbcInternal(
            Optional<ConnectionProvider> connectionProvider, 
            Map<Class, ParamSetter> paramSetters,
            Optional<Integer> defaultFetchSize
    ) {
        this.connectionProvider = connectionProvider;
        queryConfig = new QueryConfig(
                defaultFetchSize, 
                Maps.merge(DefaultParamSetters.setters(), paramSetters)
        );
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

    public Optional<ConnectionProvider> connectionProvider() {
        return connectionProvider;
    }

}
