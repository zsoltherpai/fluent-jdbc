package org.codejargon.fluentjdbc.integration
import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.integration.providers.DataSourceConnectionProvider
import org.codejargon.fluentjdbc.api.mapper.Mappers
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers
import org.codejargon.fluentjdbc.api.query.Query
import org.codejargon.fluentjdbc.integration.testdata.Dummy
import org.codejargon.fluentjdbc.integration.testdata.DummyTool
import org.codejargon.fluentjdbc.internal.support.Maps
import org.junit.After
import org.junit.Before
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection

import static org.codejargon.fluentjdbc.integration.testdata.Dummies.getDummy1
import static org.codejargon.fluentjdbc.integration.testdata.Dummies.getDummy2
import static org.codejargon.fluentjdbc.integration.testdata.DummyTool.assertDummy
import static org.codejargon.fluentjdbc.integration.testdata.TestQuery.*

abstract class IntegrationTestRoutine extends Specification {
    private static final def dummyMapper = ObjectMappers.builder().build().forClass(Dummy.class);

    protected FluentJdbc fluentJdbc
    protected Query query

    protected abstract DataSource dataSource()

    @Before
    public void initializeFluentJdbcAndCleanUpDb() {
        fluentJdbc = new FluentJdbcBuilder()
                .connectionProvider(new DataSourceConnectionProvider(dataSource()))
                .build()
        query = fluentJdbc.query()
        removeContentAndVerify()
    }
    
    @After
    public void cleanUpDb() {
        removeContentAndVerify()
    }

    def "Insert with positional parameters"() {
        when:
        query.update(insertSqlPositional).params(dummy1.params()).run()
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper)
        then:
        assertDummy(dummy, dummy1)
    }

    def "Insert with named parameters"() {
        when:
        query.update(insertSqlNamed).namedParams(dummy1.namedParams()).run()
        then:
        Dummy dummy = fluentJdbc.query().select(selectAllSql).singleResult(dummyMapper)
        assertDummy(dummy, dummy1);
    }

    def "Batch insert with positional parameters"() {
        when:
        query
                .batch(insertSqlPositional)
                .params(DummyTool.batchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        verifyBatchResults(dummies)
    }

    def "Batch insert with named parameters"() {
        when:
        query
                .batch(insertSqlNamed)
                .namedParams(DummyTool.namedBatchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        verifyBatchResults(dummies)
    }

    def "Select with MaxRows specified"() {
        when:
        query
                .batch(insertSqlNamed)
                .namedParams(DummyTool.namedBatchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        dummies.size() == 2
        List<Dummy> partialDummies = fluentJdbc.query().select(selectAllSql).maxRows(1L).listResult(dummyMapper)
        partialDummies.size() == 1
    }
    
    protected static void createTestTable(Connection connection) {
        new FluentJdbcBuilder().build().queryOn(connection).update(createDummyTable).run()
    }

    void removeContentAndVerify() {
        query.update("DELETE FROM DUMMY").run()
        def id = fluentJdbc.query().select("SELECT id FROM DUMMY").firstResult(Mappers.singleString())
        assert !id.isPresent()
    }

    void verifyBatchResults(List<Dummy> dummies) {
        assert dummies.size() == 2
        Map<String, Dummy> dummyIndex = Maps.uniqueIndex(dummies, { d -> d.id})
        assert dummyIndex.containsKey(dummy1.id)
        assertDummy(dummyIndex.get(dummy1.id), dummy1)
        assert dummyIndex.containsKey(dummy2.id)
        assertDummy(dummyIndex.get(dummy2.id), dummy2)
    }
}
