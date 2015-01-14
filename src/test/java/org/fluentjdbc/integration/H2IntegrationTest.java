package org.fluentjdbc.integration;

import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcBuilder;
import org.fluentjdbc.api.integration.providers.DataSourceConnectionProvider;
import org.fluentjdbc.api.mapper.Mappers;
import org.fluentjdbc.api.mapper.ObjectMapperFactory;
import org.fluentjdbc.api.query.Mapper;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class H2IntegrationTest {
    static Connection sentry;
    static DataSource h2DataSource;
    static FluentJdbc fluentJdbc;
    static final ObjectMapperFactory objectMappers = ObjectMapperFactory.builder().build();
    static final Mapper<Dummy> dummyMapper = objectMappers.forClass(Dummy.class);

    @BeforeClass
    public static void initH2() throws Exception {
        initH2DataSource();
        initFluentJdbc();
        createDummyTable();
    }

    @Before
    public void removeDummyContentAndVerify() {
        fluentJdbc.query().update("DELETE FROM foo").run();
        Optional<String> id = fluentJdbc.query().select("SELECT id FROM foo").firstResult(Mappers.singleString());
        assertThat(id.isPresent(), is(false));
    }

    @AfterClass
    public static void closeH2() {
        try {
            sentry.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void foo() throws SQLException{
        String id = "id";
        String bar = "bar";
        fluentJdbc.query().update("INSERT INTO foo(id, bar) VALUES(?, ?)").params(id, bar).run();
        List<Dummy> dummies = fluentJdbc.query().select("SELECT * FROM foo").listResult(dummyMapper);
        assertThat(dummies.size(), is(1));
        assertThat(dummies.get(0).id, is(equalTo(id)));
        assertThat(dummies.get(0).bar, is(equalTo(bar)));

        removeDummyContentAndVerify();
    }



    private static void initFluentJdbc() {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(new DataSourceConnectionProvider(h2DataSource))
                .build();
    }

    private static void initH2DataSource() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver").newInstance();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test/test");
        ds.setUser("sa");
        ds.setPassword("sa");
        h2DataSource = ds;
        // keep h2 instance open for the duration of the test
        sentry = ds.getConnection();
    }

    private static void createDummyTable() {
        fluentJdbc.query().update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(1023))").run();
    }

    public static class Dummy {
        String id;
        String bar;
    }



}
