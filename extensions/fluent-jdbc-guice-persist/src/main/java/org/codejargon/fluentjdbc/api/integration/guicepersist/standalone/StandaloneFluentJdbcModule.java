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
    private final DataSource dataSource;
    private final FluentJdbcBuilder fluentJdbcBuilder;

    public StandaloneFluentJdbcModule(
            FluentJdbcBuilder fluentJdbcBuilder, DataSource dataSource
    ) {
        Preconditions.checkNotNull(fluentJdbcBuilder, "fluentJdbcBuilder");
        Preconditions.checkNotNull(fluentJdbcBuilder, "dataSource");
        this.fluentJdbcBuilder = fluentJdbcBuilder;
        this.dataSource = dataSource;
    }

    @Override
    protected void configure() {
        StandaloneTxConnectionProvider cp = new StandaloneTxConnectionProvider(dataSource);
        FluentJdbc fluentJdbc = fluentJdbcBuilder.connectionProvider(cp).build();
        bindFluentJdbc(fluentJdbc);
        bindTransactionInterceptors(cp);
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
