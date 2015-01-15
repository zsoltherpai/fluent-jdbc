package org.fluentjdbc.internal;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcException;
import org.fluentjdbc.api.ParamSetter;
import org.fluentjdbc.api.integration.ConnectionProvider;
import org.fluentjdbc.api.query.Query;
import org.fluentjdbc.internal.query.ParamAssigner;
import org.fluentjdbc.internal.query.QueryInternal;
import org.fluentjdbc.internal.query.namedparameter.TransformedSql;

import static org.fluentjdbc.internal.support.Maps.merge;

public class FluentJdbcInternal implements FluentJdbc {

    private final Optional<ConnectionProvider> connectionProvider;
    private final ParamAssigner paramAssigner;
    private final Map<String, TransformedSql> namedParamSqlCache;

    public FluentJdbcInternal(Optional<ConnectionProvider> connectionProvider, Map<Class, ParamSetter> paramSetters) {
        this.connectionProvider = connectionProvider;
        this.paramAssigner = new ParamAssigner(
                merge(DefaultParamSetters.setters(), paramSetters)
        );
        namedParamSqlCache = new ConcurrentHashMap<>();
    }

    @Override
    public Query query() {
        if (!connectionProvider.isPresent()) {
            throw new FluentJdbcException("ConnectionProvider is not set.");
        }
        return new QueryInternal(connectionProvider.get(), paramAssigner, namedParamSqlCache);
    }

    @Override
    public Query queryOn(Connection connection) {
        return new QueryInternal(query -> query.receive(connection), paramAssigner, namedParamSqlCache);
    }
}
