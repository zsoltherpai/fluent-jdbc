package org.codejargon.fluentjdbc.integration.vendor
import oracle.jdbc.pool.OracleDataSource
import org.codejargon.fluentjdbc.integration.IntegrationTest
import org.codejargon.fluentjdbc.integration.IntegrationTestRoutine
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.experimental.categories.Category

import javax.sql.DataSource
import java.sql.Connection

@Category(IntegrationTest.class)
@Ignore
// needs a running instance of oracle. docker run -d -p 49160:22 -p 49161:1521 -e ORACLE_ALLOW_REMOTE=true wnameless/oracle-xe-11g
// db inspection features hang
public class OracleIntegrationTest extends IntegrationTestRoutine {
    static Connection sentry
    static DataSource oracleDataSource

    @BeforeClass
    static void initH2() {
        initOracleDs()
        createTestTable(sentry)
    }

    @AfterClass
    static void closeH2() {
        sentry.close()
    }

    def static initOracleDs() {
        OracleDataSource ods = new OracleDataSource()
        ods.setURL("jdbc:oracle:thin:system/oracle@docker:49161:xe")
        oracleDataSource = ods
        sentry = ods.getConnection()
    }

    @Override
    protected DataSource dataSource() {
        return oracleDataSource
    }

    def "Database inspection with access"() {

    }

    def "Database inspection with select"() {

    }
}
