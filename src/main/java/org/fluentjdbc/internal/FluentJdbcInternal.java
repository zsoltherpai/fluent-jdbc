package org.fluentjdbc.internal;

import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcException;
import org.fluentjdbc.api.ParamSetter;
import org.fluentjdbc.api.integration.ConnectionProvider;
import org.fluentjdbc.api.query.Query;
import org.fluentjdbc.internal.query.ParamAssigner;
import org.fluentjdbc.internal.query.QueryInternal;
import static org.fluentjdbc.internal.support.Maps.merge;

public class FluentJdbcInternal implements FluentJdbc {

    private final Optional<ConnectionProvider> connectionProvider;
    private final ParamAssigner paramAssigner;

    public FluentJdbcInternal(Optional<ConnectionProvider> connectionProvider, Map<Class, ParamSetter> paramSetters) {
        this.connectionProvider = connectionProvider;
        this.paramAssigner = new ParamAssigner(
                merge(DefaultParamSetters.setters(), paramSetters)
        );
    }

    @Override
    public Query query() {
        if (!connectionProvider.isPresent()) {
            throw new FluentJdbcException("ConnectionProvider is not set.");
        }
        return new QueryInternal(connectionProvider.get(), paramAssigner);
    }

    @Override
    public Query queryOn(Connection connection) {
        return new QueryInternal(query -> query.receive(connection), paramAssigner);
    }
}
