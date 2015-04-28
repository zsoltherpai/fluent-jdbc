package org.codejargon.fluentjdbc.api.integration.guicepersist.jpa.vendors;

import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.integration.guicepersist.TransactionTestRoutine;
import org.codejargon.fluentjdbc.api.integration.guicepersist.jpa.JpaTestInitialization;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category

import java.sql.Connection;

@Category(IntegrationTest.class)
public class EclipseLinkTest extends TransactionTestRoutine {
    private static JpaTestInitialization jpaTestInitialization
    
    @BeforeClass
    public static void init() {
        jpaTestInitialization = new JpaTestInitialization(
                "EclipseLink", 
                { em -> em.unwrap(Connection.class) }
        )
    }

    @AfterClass
    public static void stopJpa() {
        jpaTestInitialization.stop()
    }

    @Override
    protected Injector injector() {
        return jpaTestInitialization.injector()
    }
}
