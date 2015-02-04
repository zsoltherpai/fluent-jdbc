package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;

public class JpaTestInitialization {
    private final Injector injector;
    
    public JpaTestInitialization(
            String persistenceUnit,
            JpaConnectionExtractor jpaConnectionExtractor
    ) {
        JpaFluentJdbcModule fluentJdbcModule = new JpaFluentJdbcModule(
                new FluentJdbcBuilder(),
                jpaConnectionExtractor
        );
        injector = Guice.createInjector(jpaPersistModule(persistenceUnit), fluentJdbcModule);
        persistService().start();
    }
    
    public Injector injector() {
        return injector;
    }
    
    public void stop() {
        persistService().stop();
    }

    private JpaPersistModule jpaPersistModule(String persistenceUnit) {
        JpaPersistModule jpaPersistModule = new JpaPersistModule(persistenceUnit);
        jpaPersistModule.properties(PersistenceProperties.props);
        return jpaPersistModule;
    }

    private PersistService persistService() {
        return injector.getInstance(PersistService.class);
    }
}
