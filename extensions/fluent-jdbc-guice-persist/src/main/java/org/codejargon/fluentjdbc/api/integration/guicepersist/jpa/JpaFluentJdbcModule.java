package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

import com.google.inject.*;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.internal.support.Preconditions;
import javax.persistence.EntityManager;

/**
 * <p>Binds FluentJdbc and Query using a JPA ConnectionProvider. Requires an already bound EntityManager.</p>
 * Example<br><br>
 * <pre>
 * FluentJdbcBuilder fluentJdbcBuilder = new FluentJdbcBuilder(); // ... configure if needed
 * JpaConnectionExtractor extractor = ... 
 * JpaFluentJdbcModule module = new JpaFluentJdbcModule(fluentJdbcBuilder, extractor);
 * </pre>  
 * @see org.codejargon.fluentjdbc.api.integration.guicepersist.jpa.JpaConnectionExtractor
 */
public class JpaFluentJdbcModule extends AbstractModule {
    private final FluentJdbcBuilder fluentJdbcBuilder;
    private final JpaConnectionExtractor jpaConnectionExtractor;

    /**
     * Constructs a JpaFluentJdbcModule based on an already configured/customized FluentJdbcBuilder. 
     * The ConnectionProvider doesn't need to be set, the module will override that.
     *
     * @param fluentJdbcBuilder an already configured FluentJdbcBuilder
     * @param jpaConnectionExtractor implementation for the current JPA vendor
     */
    public JpaFluentJdbcModule(FluentJdbcBuilder fluentJdbcBuilder, JpaConnectionExtractor jpaConnectionExtractor) {
        Preconditions.checkNotNull(fluentJdbcBuilder, "fluentJdbcBuilder");
        Preconditions.checkNotNull(jpaConnectionExtractor, "jpaConnectionExtractor");
        this.fluentJdbcBuilder = fluentJdbcBuilder;
        this.jpaConnectionExtractor = jpaConnectionExtractor;
    }

    @Override
    protected void configure() {
        requireBinding(Key.get(EntityManager.class));
    }

    @Provides @Singleton
    FluentJdbc fluentJdbc(Provider<EntityManager> entityManagerProvider) {
        return fluentJdbcBuilder
            .connectionProvider(new JpaConnectionProvider(entityManagerProvider, jpaConnectionExtractor))
            .build();
    }
    
    @Provides @Singleton
    Query query(FluentJdbc fluentJdbc) {
        return fluentJdbc.query();
    }
}
