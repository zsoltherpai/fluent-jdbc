package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa;

import com.google.inject.Provider;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.integration.QueryConnectionReceiver;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

class JpaConnectionProvider implements ConnectionProvider {
    private final Provider<EntityManager> entityManagerProvider;
    private final JpaConnectionExtractor jpaConnectionExtractor;

    JpaConnectionProvider(Provider<EntityManager> entityManagerProvider, JpaConnectionExtractor jpaConnectionExtractor) {
        this.entityManagerProvider = entityManagerProvider;
        this.jpaConnectionExtractor = jpaConnectionExtractor;
    }

    @Override
    public void provide(QueryConnectionReceiver query) throws SQLException {
        EntityManager entityManager = entityManagerProvider.get();
        if(entityManager.getTransaction().isActive()) {
            query.receive(extractConnection(entityManager));
        } else {
            provideInNewTx(query, entityManager);
        }
    }
    private void provideInNewTx(QueryConnectionReceiver query, EntityManager entityManager) throws SQLException {
        entityManager.getTransaction().begin();
        try {
            query.receive(extractConnection(entityManager));
            entityManager.getTransaction().commit();
        } catch(Exception e) {
            entityManager.getTransaction().rollback();
            throw new FluentJdbcException("Error providing connection to FluentJdbc", e);
        }
    }

    private Connection extractConnection(EntityManager entityManager) {
        try {
            Connection connection = jpaConnectionExtractor.extract(entityManager);
            Preconditions.checkArgument(connection != null, "Connection returned by the provided JpaConnectionExtractor is null");
            return jpaConnectionExtractor.extract(entityManager);
        } catch (Exception e) {
            throw new FluentJdbcException("Can't extract connection from EntityManager by the provided JpaConnectionExtractor", e);
        }
    }
    

}
