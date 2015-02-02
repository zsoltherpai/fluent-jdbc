package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class TransactionTestDb {
    static DataSource ds;
    static Connection sentry;

    @BeforeClass
    public static void initDb() throws Exception {
        JdbcDataSource h2Ds = dataSource();
        // keep a connection open for the duration of the test
        sentry = h2Ds.getConnection();
        createTestTable();
    }

    @Before
    public void clearTestTable() {
        queryOnSentry().update("DELETE FROM DUMMY").run();
    }

    @AfterClass
    public static void closeDb() {
        try {
            sentry.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static JdbcDataSource dataSource() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class.forName("org.h2.Driver").newInstance();
        JdbcDataSource h2Ds = new JdbcDataSource();
        h2Ds.setURL("jdbc:h2:mem:test/test");
        h2Ds.setUser("sa");
        h2Ds.setPassword("sa");
        ds = h2Ds;
        return h2Ds;
    }

    private static void createTestTable() {
        queryOnSentry().update("CREATE TABLE DUMMY (id VARCHAR(255) PRIMARY KEY)").run();
    }
    
    static Query queryOnSentry() {
        return new FluentJdbcBuilder().build().queryOn(sentry);
    }
}
