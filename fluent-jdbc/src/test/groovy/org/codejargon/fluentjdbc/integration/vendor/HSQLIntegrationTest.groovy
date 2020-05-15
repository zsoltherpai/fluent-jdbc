package org.codejargon.fluentjdbc.integration.vendor

import org.codejargon.fluentjdbc.api.mapper.Mappers
import org.codejargon.fluentjdbc.integration.IntegrationTest
import org.codejargon.fluentjdbc.integration.IntegrationTestRoutine
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.experimental.categories.Category

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager
import java.util.logging.Logger

@Category(IntegrationTest.class)
class HSQLIntegrationTest extends IntegrationTestRoutine {
    static def connectionString = "jdbc:hsqldb:mem:testdb"
    static Connection sentry
    static DataSource hsqlDataSource

    @BeforeClass
    static void initHsql() {
        initH2DataSource()
        createTestTable(sentry, "VARBINARY(100)")
    }

    @AfterClass
    static void closeHsql() {
            sentry.close()
    }

    def "Batch insert auto-generated keys fetch"() {
        given:
        query.update("CREATE TABLE DUMMY_AUTO (id INTEGER IDENTITY PRIMARY KEY, data VARCHAR(255));").run()
        when:
        def result = query.batch("INSERT INTO DUMMY_AUTO(DATA) VALUES(?)").params([["a"], ["b"]]).runFetchGenKeys(Mappers.singleLong())
        then:
        result.size() == 2
        result.get(0).generatedKeys().size() == 1
        result.get(0).generatedKeys().get(0) == 0
        result.get(1).generatedKeys().size() == 1
        result.get(1).generatedKeys().get(0) == 1
    }

    private static void initH2DataSource() {
        Class.forName("org.hsqldb.jdbcDriver")
        hsqlDataSource = new HsqlDatasource()
        // keep a connection open for the duration of the tests
        sentry = hsqlDataSource.getConnection()
    }
    

    @Override
    protected DataSource dataSource() {
        return hsqlDataSource
    }
    
    private static class HsqlDatasource implements DataSource {
        @Override
        public Connection getConnection() {
            return DriverManager.getConnection(connectionString, "SA", "")
        }

        @Override
        public Connection getConnection(String username, String password) {
            throw new UnsupportedOperationException()
        }

        @Override
        public PrintWriter getLogWriter() {
            throw new UnsupportedOperationException()
        }

        @Override
        public void setLogWriter(PrintWriter out) {
            throw new UnsupportedOperationException()
        }

        @Override
        public void setLoginTimeout(int seconds) {
            throw new UnsupportedOperationException()
        }

        @Override
        public int getLoginTimeout() {
            throw new UnsupportedOperationException()
        }

        @Override
        public Logger getParentLogger() {
            throw new UnsupportedOperationException()
        }

        @Override
        public <T> T unwrap(Class<T> iface) {
            throw new UnsupportedOperationException()
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            throw new UnsupportedOperationException()
        }
    }
}
