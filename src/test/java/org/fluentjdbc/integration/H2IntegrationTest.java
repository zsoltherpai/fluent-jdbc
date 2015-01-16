package org.fluentjdbc.integration;

import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcBuilder;
import org.fluentjdbc.api.integration.providers.DataSourceConnectionProvider;
import org.fluentjdbc.api.mapper.Mappers;
import org.fluentjdbc.api.mapper.ObjectMapperFactory;
import org.fluentjdbc.api.query.Mapper;
import org.fluentjdbc.internal.support.Arrs;
import org.fluentjdbc.internal.support.Maps;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class H2IntegrationTest {
    static final ObjectMapperFactory objectMappers = ObjectMapperFactory.builder().build();
    static final Mapper<Dummy> dummyMapper = objectMappers.create(Dummy.class);
    static final Dummy dummy1 = new Dummy("idValue1", "barValue1");
    static final Dummy dummy2 = new Dummy("idValue2", "barValue2");

    static Connection sentry;
    static DataSource h2DataSource;
    static FluentJdbc fluentJdbc;

    @BeforeClass
    public static void initH2() throws Exception {
        initH2DataSource();
        initFluentJdbc();
        createDummyTable();
    }

    @Before
    @After
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
    public void insertWithPositional() throws SQLException{
        fluentJdbc.query().update("INSERT INTO foo(id, bar) VALUES(?, ?)").params(dummy1.id, dummy1.bar).run();
        Dummy dummy = fluentJdbc.query().select("SELECT * FROM foo").singleResult(dummyMapper);
        verifyDummy(dummy, dummy1);
    }

    @Test
    public void insertWithNamedParams() throws SQLException{
        Map<String, Object> params = new HashMap<>();
        params.put("id", dummy1.id);
        params.put("bar", dummy1.bar);

        fluentJdbc.query().update("INSERT INTO foo(id, bar) VALUES(:id, :bar)").namedParams(params).run();
        Dummy dummy = fluentJdbc.query().select("SELECT * FROM foo").singleResult(dummyMapper);

        verifyDummy(dummy, dummy1);
    }

    @Test
    public void batchInsertWithPositionalParams() {
        fluentJdbc.query()
                .batch("INSERT INTO foo(id, bar) VALUES(?, ?)")
                .params(batchParamsFor(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select("SELECT * FROM foo").listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    @Test
         public void batchInsertWithNamedParams() {
        fluentJdbc.query()
                .batch("INSERT INTO foo(id, bar) VALUES(:id, :bar)")
                .namedParams(namedBatchParamsFor(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select("SELECT * FROM foo").listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    private void verifyBatchResults(List<Dummy> dummies) {
        assertThat(dummies.size(), is(2));
        Map<String, Dummy> dummyIndex = Maps.uniqueIndex(dummies, dummy -> dummy.id);
        assertThat(dummyIndex.containsKey(dummy1.id), is(true));
        verifyDummy(dummyIndex.get(dummy1.id), dummy1);

        assertThat(dummyIndex.containsKey(dummy2.id), is(true));
        verifyDummy(dummyIndex.get(dummy2.id), dummy2);
    }

    private void verifyDummy(Dummy actual, Dummy expected) {
        assertThat(actual.id, is(equalTo(expected.id)));
        assertThat(actual.bar, is(equalTo(expected.bar)));
    }

    private Iterator<Map<String, Object>> namedBatchParamsFor(Dummy... dummies) {
        List<Map<String, Object>> allParams = new ArrayList<>();
        Arrs.stream(dummies).forEach(
                dummy -> {
                    Map<String, Object> params = new HashMap<>();
                    params.put("id", dummy.id);
                    params.put("bar", dummy.bar);
                    allParams.add(params);
                }
        );
        return allParams.iterator();
    }

    private Iterator<List<Object>> batchParamsFor(Dummy... dummies) {
        List<List<Object>> allParams = new ArrayList<>();
        Arrs.stream(dummies).forEach(
                dummy -> {
                    List<Object> params = new ArrayList();
                    params.add(dummy.id);
                    params.add(dummy.bar);
                    allParams.add(params);
                }
        );
        return allParams.iterator();
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
        // keep one single h2 instance open for the duration of the test
        sentry = ds.getConnection();
    }

    private static void createDummyTable() {
        fluentJdbc.query().update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(1023))").run();
    }

    public static class Dummy {
        String id;
        String bar;

        public Dummy(String id, String bar) {
            this.id = id;
            this.bar = bar;
        }

        public Dummy() {
        }
    }



}
