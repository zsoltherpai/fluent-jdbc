package org.codejargon.fluentjdbc.integration;

import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class H2IntegrationTest extends IntegrationTestDefinition {

    static Connection sentry;
    static DataSource h2DataSource;

    @BeforeClass
    public static void initH2() throws Exception {
        initH2DataSource();
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

    private static void initH2DataSource() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver").newInstance();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test/test");
        ds.setUser("sa");
        ds.setPassword("sa");
        h2DataSource = ds;
        // keep one single h2 instance open for the duration of the test
        sentry = ds.getConnection();
    }

    private static void createDummyTable() {
        new FluentJdbcBuilder().build().queryOn(sentry).update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(1023))").run();
    }

    @Override
    protected DataSource dataSource() {
        return h2DataSource;
    }
}
