package org.codejargon.fluentjdbc.integration.vendor;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.codejargon.fluentjdbc.integration.IntegrationTestDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Category(IntegrationTest.class)
public class DerbyIntegrationTest extends IntegrationTestDefinition {
    static Connection sentry;
    static DataSource derbyDataSource;

    @BeforeClass
    public static void initH2() throws Exception {
        initDerbyDataSource();
        createDummyTable();
    }

    @AfterClass
    public static void closeH2() {
        try {
            sentry.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initDerbyDataSource() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:test");
        ds.setCreateDatabase("create");
        derbyDataSource = ds;
        sentry = ds.getConnection();
    }

    private static void createDummyTable() {
        new FluentJdbcBuilder().build().queryOn(sentry).update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(1023))").run();
    }

    @Override
    protected DataSource dataSource() {
        return derbyDataSource;
    }
}
