package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.AbstractModule;
import com.google.inject.persist.Transactional;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.internal.FluentJdbcInternal;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import javax.sql.DataSource;
import java.util.Optional;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkPresent;

/**
 * <p>Guice module for binding of FluentJdbc and Query interfaces, and @Transactional interceptors. FluentJdbc can be
 * customized via FluentJdbcBuilder. The connection provider doesn't need to be set, it will be overridden by the module.</p>
 * Example:<br><br>
 * <pre>
 * FluentJdbcBuilder fluentJdbcBuilder = new FluentJdbcBuilder(); // ... configure if needed
 * DataSource dataSource = ...
 * StandaloneFluentJdbcModule module = new StandaloneFluentJdbcModule(fluentJdbcBuilder, dataSource);
 * </pre>
 */
public class StandaloneFluentJdbcModule extends AbstractModule {
    private final StandaloneTxConnectionProvider connectionProvider;
    private final FluentJdbcBuilder fluentJdbcBuilder;

    public StandaloneFluentJdbcModule(
            FluentJdbcBuilder fluentJdbcBuilder, DataSource dataSource
    ) {
        Preconditions.checkNotNull(fluentJdbcBuilder, "fluentJdbcBuilder");
        Preconditions.checkNotNull(dataSource, "dataSource");
        this.fluentJdbcBuilder = fluentJdbcBuilder;
        this.connectionProvider = new StandaloneTxConnectionProvider(dataSource);
    }

    /**
     * Constructs StandaloneFluentJdbcModule based on a custom ConnectionProvider. Warning: the ConnectionProvider
     * implementation must keep the Connection open after providing it to the query (closing would disrupt transaction management)
     * @param fluentJdbcBuilder Fluent-Jdbc configuration
     * @param connectionProvider an implementation that must keep the connection open after providing it to the Query
     */
    public StandaloneFluentJdbcModule(
            FluentJdbcBuilder fluentJdbcBuilder, ConnectionProvider connectionProvider
    ) {
        Preconditions.checkNotNull(fluentJdbcBuilder, "fluentJdbcBuilder");
        Preconditions.checkNotNull(connectionProvider, "connectionProvider");
        this.fluentJdbcBuilder = fluentJdbcBuilder;
        this.connectionProvider = new StandaloneTxConnectionProvider(connectionProvider);
    }
    

    @Override
    protected void configure() {
        FluentJdbc fluentJdbc = fluentJdbcBuilder.connectionProvider(connectionProvider).build();
        bindFluentJdbc(fluentJdbc);
        bindTransactionInterceptors(connectionProvider);
    }

    private void bindFluentJdbc(FluentJdbc fluentJdbc) {
        bind(FluentJdbc.class).toInstance(fluentJdbc);
        bind(Query.class).toInstance(fluentJdbc.query());
    }

    private void bindTransactionInterceptors(StandaloneTxConnectionProvider cp) {
        TransactionInterceptor interceptor = new TransactionInterceptor(cp);
        bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);
        bindInterceptor(annotatedWith(Transactional.class), any(), interceptor);
    }
}
