package org.codejargon.fluentjdbc.api;

import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.internal.FluentJdbcInternal;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

/**
 * Configures and builds a FluentJdbc instance.
 */
public class FluentJdbcBuilder {
    private Optional<ConnectionProvider> connectionProvider = Optional.empty();
    private Map<Class, ParamSetter> paramSetters = Maps.copyOf(new HashMap<>());

    public FluentJdbcBuilder() {
        
    }

    /**
     * Sets the ConnectionProvider for FluentJdbc. Queries created by fluentJdbc.query() will use
     * Connections returned by this provider.
     *
     * @param connectionProvider ConnectionProvider implementation
     * @return this
     */
    public FluentJdbcBuilder connectionProvider(ConnectionProvider connectionProvider) {
        checkNotNull(connectionProvider, "connnectionProvider");
        this.connectionProvider = Optional.of(connectionProvider);
        return this;
    }

    /**
     * ParamSetters add support for accepting parameters of custom types in all queries (select/update/insert/batch).
     * These setters can also override types supported by FluentJdbc out of the box (JDBC-supported types, 
     * java.util.Date, java.time)
     * @param paramSetters Map of parameter class / ParamSetters pairs.
     * @return this
     */
    public FluentJdbcBuilder paramSetters(Map<Class, ParamSetter> paramSetters) {
        checkNotNull(paramSetters, "paramSetters");
        this.paramSetters = paramSetters;
        return this;
    }

    /**
     * Returns a FluentJdbc instance configured by the builder.
     * @return FluentJdbc instance
     */
    public FluentJdbc build() {
        return new FluentJdbcInternal(connectionProvider, Maps.copyOf(paramSetters));
    }
}
