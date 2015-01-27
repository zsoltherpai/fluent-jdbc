package org.codejargon.fluentjdbc.integration.vendor;

import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.codejargon.fluentjdbc.integration.IntegrationTestDefinition;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Category(IntegrationTest.class)
public class HSQLIntegrationTest extends IntegrationTestDefinition {
    static String connectionString = "jdbc:hsqldb:mem:testdb";
    static Connection sentry;
    static DataSource hsqlDataSource;

    @BeforeClass
    public static void initHsql() throws Exception {
        initH2DataSource();
        createDummyTable();
    }

    @AfterClass
    public static void closeHsql() {
        try {
            sentry.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initH2DataSource() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbcDriver");
        hsqlDataSource = new HsqlDatasource();
        sentry = hsqlDataSource.getConnection();
    }

    private static void createDummyTable() {
        new FluentJdbcBuilder().build().queryOn(sentry).update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(1023))").run();
    }

    @Override
    protected DataSource dataSource() {
        return hsqlDataSource;
    }
    
    private static class HsqlDatasource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(connectionString, "SA", "");
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }
}
