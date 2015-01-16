package org.codejargon.fluentjdbc.api;

import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.internal.FluentJdbcInternal;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

public class FluentJdbcBuilder {
    private Optional<ConnectionProvider> connectionProvider = Optional.empty();
    private Map<Class, ParamSetter> paramSetters = Maps.copyOf(new HashMap<>());

    public FluentJdbcBuilder() {
        
    }
    
    public FluentJdbcBuilder connectionProvider(ConnectionProvider connectionProvider) {
        checkNotNull(connectionProvider, "connnectionProvider");
        this.connectionProvider = Optional.of(connectionProvider);
        return this;
    }
    
    public FluentJdbcBuilder paramSetters(Map<Class, ParamSetter> paramSetters) {
        checkNotNull(paramSetters, "paramSetters");
        this.paramSetters = paramSetters;
        return this;
    }
    
    public FluentJdbc build() {
        return new FluentJdbcInternal(connectionProvider, Maps.copyOf(paramSetters));
    }
}
