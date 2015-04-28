package org.codejargon.fluentjdbc.integration.vendor
import org.apache.derby.jdbc.EmbeddedDataSource
import org.codejargon.fluentjdbc.integration.IntegrationTest
import org.codejargon.fluentjdbc.integration.IntegrationTestRoutine
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.experimental.categories.Category

import javax.sql.DataSource
import java.sql.Connection

@Category(IntegrationTest.class)
class DerbyIntegrationTest extends IntegrationTestRoutine {
    static Connection sentry
    static DataSource derbyDataSource

    @BeforeClass
    static void initH2() {
        initDerbyDataSource()
        createTestTable(sentry)
    }

    @AfterClass
    static void closeH2() {
        sentry.close()
    }

    private static void initDerbyDataSource() {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance()
        EmbeddedDataSource ds = new EmbeddedDataSource()
        ds.setDatabaseName("memory:test")
        ds.setCreateDatabase("create")
        derbyDataSource = ds
        sentry = ds.getConnection()
    }

    @Override
    protected DataSource dataSource() {
        return derbyDataSource;
    }
}

