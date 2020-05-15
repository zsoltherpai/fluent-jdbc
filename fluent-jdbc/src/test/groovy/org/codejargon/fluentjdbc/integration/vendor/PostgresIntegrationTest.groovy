package org.codejargon.fluentjdbc.integration.vendor

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.codejargon.fluentjdbc.integration.IntegrationTestRoutine
import org.junit.AfterClass
import org.junit.BeforeClass

import javax.sql.DataSource

class PostgresIntegrationTest extends IntegrationTestRoutine {
    static EmbeddedPostgres pg
    static DataSource ds

    @BeforeClass
    static void initPostgres() {
        pg = EmbeddedPostgres.start();
        ds = pg.getDatabase("postgres", "postgres")

        def con = null
        try {
            con = ds.getConnection()
            createTestTable(con)
        } finally {
            con.close()
        }
    }

    @AfterClass
    static void closePostgres() {
        pg.close()
    }

    @Override
    protected DataSource dataSource() {
        return ds
    }
}
