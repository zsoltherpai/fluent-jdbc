package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.integration.guicepersist.TransactionTestRoutine;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;

@Category(IntegrationTest.class)
public class StandaloneTransactionTest extends TransactionTestRoutine {
    private static Injector injector;
    
    @BeforeClass
    public static void initFluentJdbcAndTestService() throws Exception {
        FluentJdbcBuilder fluentJdbc = new FluentJdbcBuilder();
        injector = Guice.createInjector(new StandaloneFluentJdbcModule(fluentJdbc, h2DataSource()));
    }

    private static DataSource h2DataSource() throws Exception {
        Class.forName("org.h2.Driver").newInstance();
        JdbcDataSource h2Ds = new JdbcDataSource();
        h2Ds.setURL("jdbc:h2:mem:test/test;DB_CLOSE_DELAY=-1");
        h2Ds.setUser("sa");
        h2Ds.setPassword("sa");
        return h2Ds;
    }

    @Override
    protected Injector injector() {
        return injector;
    }
}
