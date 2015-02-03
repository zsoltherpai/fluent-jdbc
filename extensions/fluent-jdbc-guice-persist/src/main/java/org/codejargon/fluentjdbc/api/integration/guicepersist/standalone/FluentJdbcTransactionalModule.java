package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.AbstractModule;
import com.google.inject.persist.Transactional;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;

import java.util.Optional;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkPresent;

/**
 * <p>Guice module for binding of FluentJdbc and Query interfaces, and @Transactional interceptors. FluentJdbc must be
 * configured to use a TransactionalConnectionProvider.</p>
 * Example:<br><br>
 * <pre>
 * TransactionalConnectionProvider cp = new TransactionalConnectionProvider(ds);
 * FluentJdbc fluentJdbc = new FluentJdbcBuilder().connectionProvider(cp).build();
 * Injector injector = Guice.createInjector(new FluentJdbcTransactionalModule(fluentJdbc), ...);
 * </pre>
 * @see org.codejargon.fluentjdbc.api.integration.guicepersist.standalone.TransactionalConnectionProvider
 */
public class FluentJdbcTransactionalModule extends AbstractModule {
    private final TransactionalConnectionProvider transactionalConnectionProvider;
    private final FluentJdbc fluentJdbc;

    public FluentJdbcTransactionalModule(
            FluentJdbc fluentJdbc
    ) {
        this.fluentJdbc = fluentJdbc;
        Optional<ConnectionProvider> cp = fluentJdbc.connectionProvider();
        verifyConnectionProvider(cp);
        transactionalConnectionProvider = (TransactionalConnectionProvider) cp.get();
    }

    private void verifyConnectionProvider(Optional<ConnectionProvider> cp) {
        checkPresent(cp, "ConnectionProvider");
        if(!(cp.get() instanceof TransactionalConnectionProvider)) {
            throw new FluentJdbcException(String.format("ConnectionProvider must be an instance of TransactionalConnectionProvider, actual class: %s", cp.get().getClass()));
        }
    }

    @Override
    protected void configure() {
        bindFluentJdbc();
        bindTransactionInterceptors();
    }

    private void bindFluentJdbc() {
        bind(FluentJdbc.class).toInstance(fluentJdbc);
        bind(Query.class).toInstance(fluentJdbc.query());
    }

    private void bindTransactionInterceptors() {
        TransactionInterceptor interceptor = new TransactionInterceptor(transactionalConnectionProvider);
        bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);
        bindInterceptor(annotatedWith(Transactional.class), any(), interceptor);
    }
}
