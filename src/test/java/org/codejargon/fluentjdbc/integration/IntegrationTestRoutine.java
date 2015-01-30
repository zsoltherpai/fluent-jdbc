package org.codejargon.fluentjdbc.integration;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.integration.providers.DataSourceConnectionProvider;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.integration.testdata.Dummy;
import org.codejargon.fluentjdbc.internal.support.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.codejargon.fluentjdbc.integration.testdata.DummyTool.*;
import static org.codejargon.fluentjdbc.integration.testdata.TestQuery.*;
import static org.codejargon.fluentjdbc.integration.testdata.Dummies.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class IntegrationTestRoutine {
    private static final Mapper<Dummy> dummyMapper = ObjectMappers.builder().build().forClass(Dummy.class);

    
    protected FluentJdbc fluentJdbc;
    protected Query query;

    protected abstract DataSource dataSource();

    @Before
    public void initializeFluentJdbcAndCleanUpDb() {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(new DataSourceConnectionProvider(dataSource()))
                .build();
        query = fluentJdbc.query();
        removeContentAndVerify();
    }
    
    @After
    public void cleanUp() {
        removeContentAndVerify();
    }

    @Test
    public void insertWithPositional() throws SQLException {
        query.update(insertSqlPositional).params(params(dummy1)).run();
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper);
        verifyDummy(dummy, dummy1);
    }

    @Test
    public void insertWithNamedParams() throws SQLException{
        query.update(insertSqlNamed).namedParams(namedParams(dummy1)).run();
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper);
        verifyDummy(dummy, dummy1);
    }

    @Test
    public void batchInsertWithPositionalParams() {
        query
                .batch(insertSqlPositional)
                .params(batchParams(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    @Test
    public void batchInsertWithNamedParams() {
        query
                .batch(insertSqlNamed)
                .namedParams(namedBatchParams(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        verifyBatchResults(dummies);
    }

    @Test
    public void maxRows() {
        query
                .batch(insertSqlNamed)
                .namedParams(namedBatchParams(dummy1, dummy2))
                .run();
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper);
        assertThat(dummies.size(), is(2));
        List<Dummy> partialDummies = fluentJdbc.query().select(selectAllSql).maxRows(1L).listResult(dummyMapper);
        assertThat(partialDummies.size(), is(1));
    }
    
    protected static void createTestTable(Connection connection) {
        new FluentJdbcBuilder().build().queryOn(connection).update(createDummyTable).run();
    }

    private void removeContentAndVerify() {
        query.update("DELETE FROM DUMMY").run();
        Optional<String> id = fluentJdbc.query().select("SELECT id FROM DUMMY").firstResult(Mappers.singleString());
        assertThat(id.isPresent(), is(false));
    }

    private void verifyBatchResults(List<Dummy> dummies) {
        assertThat(dummies.size(), is(2));
        Map<String, Dummy> dummyIndex = Maps.uniqueIndex(dummies, Dummy::id);
        assertThat(dummyIndex.containsKey(dummy1.id()), is(true));
        verifyDummy(dummyIndex.get(dummy1.id()), dummy1);
        assertThat(dummyIndex.containsKey(dummy2.id()), is(true));
        verifyDummy(dummyIndex.get(dummy2.id()), dummy2);
    }


}
