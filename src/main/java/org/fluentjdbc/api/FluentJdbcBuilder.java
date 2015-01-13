package org.fluentjdbc.api;

import org.fluentjdbc.api.integration.ConnectionProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.fluentjdbc.internal.FluentJdbcInternal;
import org.fluentjdbc.internal.support.Maps;
import static org.fluentjdbc.internal.support.Preconditions.checkNotNull;

public class FluentJdbcBuilder {
    private Optional<ConnectionProvider> connectionProvider = Optional.empty();
    private Map<Class, ParamSetter> paramSetters = Maps.immutableCopyOf(new HashMap<>());

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
        return new FluentJdbcInternal(connectionProvider, Maps.immutableCopyOf(paramSetters));
    }
}
