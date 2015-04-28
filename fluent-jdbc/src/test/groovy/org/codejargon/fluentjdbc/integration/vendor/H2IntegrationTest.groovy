package org.codejargon.fluentjdbc.integration.vendor;

import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.codejargon.fluentjdbc.integration.IntegrationTestRoutine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.junit.experimental.categories.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)
class H2IntegrationTest extends IntegrationTestRoutine {

    static Connection sentry
    static DataSource h2DataSource

    @BeforeClass
    static void initH2() {
        initH2DataSource()
        createTestTable(sentry)
    }

    @AfterClass
    static void closeH2() {
        sentry.close()
    }

    def "Auto-generated keys returned"() {
        given:
        fluentJdbc.query().update("CREATE TABLE DUMMY_AUTO (id INTEGER PRIMARY KEY AUTO_INCREMENT, data VARCHAR(255));").run()
        when:
        def result = query.update("INSERT INTO DUMMY_AUTO(DATA) VALUES('bla')").runFetchGenKeys(
                Mappers.singleLong()
        )
        then:
        assert result.generatedKeys().size() == 1
        assert result.generatedKeys().get(0) == 1L
    }

    private static def initH2DataSource() {
        Class.forName("org.h2.Driver").newInstance()
        JdbcDataSource ds = new JdbcDataSource()
        ds.setURL("jdbc:h2:mem:test/test")
        ds.setUser("sa")
        ds.setPassword("sa")
        h2DataSource = ds
        // keep a connection open for the duration of the test
        sentry = ds.getConnection()
    }

    @Override
    protected DataSource dataSource() {
        return h2DataSource;
    }
}
