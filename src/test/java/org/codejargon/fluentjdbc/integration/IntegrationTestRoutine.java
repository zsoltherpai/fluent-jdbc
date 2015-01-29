package org.codejargon.fluentjdbc.integration;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.integration.providers.DataSourceConnectionProvider;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.internal.support.Arrs;
import org.codejargon.fluentjdbc.internal.support.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class IntegrationTestRoutine {
    private static final ObjectMappers objectMappers = ObjectMappers.builder().build();
    private static final Mapper<Dummy> dummyMapper = objectMappers.forClass(Dummy.class);
    private static final Dummy dummy1 = new Dummy("idValue1", "barValue1");
    private static final Dummy dummy2 = new Dummy("idValue2", "barValue2");
    
    private static final String insertSqlPositional = "INSERT INTO foo(id, bar) VALUES(?, ?)";
    private static final String insertSqlNamed = "INSERT INTO foo(id, bar) VALUES(:id, :bar)";
    private static final String selectAllSql = "SELECT * FROM foo";
    
    protected FluentJdbc fluentJdbc;

    protected abstract DataSource dataSource();

    @Before
    public void initializeFluentJdbcAndCleanUp() {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(new DataSourceConnectionProvider(dataSource()))
                .build();
        removeContentAndVerify();
    }
    
    @After
    public void cleanUp() {
        removeContentAndVerify();
    }

    @Test
    public void insertWithPositional() throws SQLException {
        fluentJdbc.query().update(insertSqlPositional).params(dummy1.id, dummy1.bar).run();
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper);
        verifyDummy(dummy, dummy1);
    }

    @Test
    public void insertWithNamedParams() throws SQLException{
        Map<String, Object> params = namedParams(dummy1);

        fluentJdbc.query().update(insertSqlNamed).namedParams(params).run();
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper);

        verifyDummy(dummy, dummy1);
    }

    @Test
    public void batchInsertWithPositionalParams() {
        fluentJdbc.query()
                .batch(insertSqlPositional)
                .params(batchParamsFor(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    @Test
    public void batchInsertWithNamedParams() {
        fluentJdbc.query()
                .batch(insertSqlNamed)
                .namedParams(namedBatchParamsFor(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    @Test
    public void maxRows() {
        fluentJdbc.query()
                .batch(insertSqlNamed)
                .namedParams(namedBatchParamsFor(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        assertThat(dummies.size(), is(2));
        List<Dummy> partialDummies = fluentJdbc.query().select(selectAllSql).maxRows(1L).listResult(dummyMapper);
        assertThat(partialDummies.size(), is(1));
    }
    
    protected static void createTestTable(Connection connection) {
        new FluentJdbcBuilder().build().queryOn(connection).update("CREATE TABLE foo (id VARCHAR(255) PRIMARY KEY, bar VARCHAR(255))").run();
    }

    private void removeContentAndVerify() {
        fluentJdbc.query().update("DELETE FROM foo").run();
        Optional<String> id = fluentJdbc.query().select("SELECT id FROM foo").firstResult(Mappers.singleString());
        assertThat(id.isPresent(), is(false));
    }

    private Map<String, Object> namedParams(Dummy dummy) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", dummy.id);
        params.put("bar", dummy.bar);
        return Maps.copyOf(params);
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
                    List<Object> params = new ArrayList<>();
                    params.add(dummy.id);
                    params.add(dummy.bar);
                    allParams.add(params);
                }
        );
        return allParams.iterator();
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
