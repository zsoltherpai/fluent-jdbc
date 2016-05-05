package org.codejargon.fluentjdbc.api;

import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.integration.providers.DataSourceConnectionProvider;
import org.codejargon.fluentjdbc.api.query.listen.AfterQueryListener;
import org.codejargon.fluentjdbc.internal.FluentJdbcInternal;
import org.codejargon.fluentjdbc.internal.support.Maps;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkArgument;
import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

/**
 * Configures and builds a FluentJdbc instance
 * 
 * @see org.codejargon.fluentjdbc.api.FluentJdbc
 */
public class FluentJdbcBuilder {
    private Optional<Integer> defaultFetchSize = Optional.empty();
    private Optional<ConnectionProvider> connectionProvider = Optional.empty();
    private Optional<AfterQueryListener> afterQueryListener = Optional.empty();
    private Map<Class, ParamSetter> paramSetters = Maps.copyOf(new HashMap<>());

    public FluentJdbcBuilder() {
        
    }

    /**
     * Sets the ConnectionProvider for FluentJdbc. Queries created by fluentJdbc.query() will use
     * Connections returned by this provider
     *
     * @param connectionProvider ConnectionProvider implementation
     * @return this
     */
    public FluentJdbcBuilder connectionProvider(ConnectionProvider connectionProvider) {
        checkNotNull(connectionProvider, "connectionProvider");
        this.connectionProvider = Optional.of(connectionProvider);
        return this;
    }

    public FluentJdbcBuilder connectionProvider(DataSource dataSource) {
        return connectionProvider(new DataSourceConnectionProvider(dataSource));
    }

    /**
     * ParamSetters add support for accepting parameters of custom types in all queries (select/update/insert/batch)
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
     * Sets default fetch size of select statements - the number of rows returned with one network roundtrip
     * Vendor default is used if not set. Note that vendor defaults may be different, eg MySQL default 
     * is 0 (no limit) which may lead to memory issues, Oracle DB's default is 10 which may result in poor 
     * performance with large ResultSets.
     * @param rows Number of rows fetched by a select statement by default
     * @return this
     */
    public FluentJdbcBuilder defaultFetchSize(Integer rows) {
        checkNotNull(rows, "rows");
        checkArgument(rows >= 0, "Fetch size rows must be >= 0");
        this.defaultFetchSize = Optional.of(rows);
        return this;
    }

    public FluentJdbcBuilder afterQueryListener(AfterQueryListener afterQueryListener) {
        this.afterQueryListener = Optional.of(afterQueryListener);
        return this;
    }

    /**
     * Returns a FluentJdbc instance configured by the builder
     * @return FluentJdbc instance
     */
    public FluentJdbc build() {
        return new FluentJdbcInternal(
                connectionProvider, 
                Maps.copyOf(paramSetters),
                defaultFetchSize,
                afterQueryListener
        );
    }
}
