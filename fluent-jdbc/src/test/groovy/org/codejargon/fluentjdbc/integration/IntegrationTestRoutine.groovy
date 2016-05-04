package org.codejargon.fluentjdbc.integration
import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.integration.providers.DataSourceConnectionProvider
import org.codejargon.fluentjdbc.api.mapper.Mappers
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers
import org.codejargon.fluentjdbc.api.query.Query
import org.codejargon.fluentjdbc.api.query.Transaction
import org.codejargon.fluentjdbc.integration.testdata.Dummies
import org.codejargon.fluentjdbc.integration.testdata.Dummy
import org.codejargon.fluentjdbc.internal.support.Maps
import org.junit.After
import org.junit.Before
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet

import static org.codejargon.fluentjdbc.integration.testdata.Dummies.*
import static org.codejargon.fluentjdbc.integration.testdata.TestQuery.*

abstract class IntegrationTestRoutine extends Specification {
    private static final def objectMappers = ObjectMappers.builder().build();
    private static final def dummyMapper = objectMappers.forClass(Dummy.class);
    private static final def dummyAliasMapper = objectMappers.forClass(DummyAlias.class);

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
                .params(Dummies.batchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        verifyBatchResults(dummies)
    }


    def "ObjectMappers supports SQL AS"() {
        when:
        query
                .batch(insertSqlPositional)
                .params(Dummies.batchParams(dummy1))
                .run()
        List<DummyAlias> dummies = fluentJdbc.query().select("SELECT ID AS ID_ALIAS FROM DUMMY").listResult(dummyAliasMapper)
        then:
        dummies.get(0).idAlias == dummy1.id

    }


    def "Batch insert with named parameters"() {
        when:
        query
                .batch(insertSqlNamed)
                .namedParams(Dummies.namedBatchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        verifyBatchResults(dummies)
    }

    def "Select with MaxRows specified"() {
        when:
        query
                .batch(insertSqlNamed)
                .namedParams(Dummies.namedBatchParams(dummy1, dummy2))
                .run()
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        dummies.size() == 2
        List<Dummy> partialDummies = fluentJdbc.query().select(selectAllSql).maxRows(1L).listResult(dummyMapper)
        partialDummies.size() == 1
    }

    def "Transaction committed"() {
        when:
        query.transaction().in({ ->
            query.update(insertSqlPositional).params(dummy1.params()).run()
            query.update(insertSqlPositional).params(dummy2.params()).run()
        });
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        dummies.size() == 2
    }

    def "Transaction rolled back"() {
        when:
        query.transaction().in({ ->
            query.update(insertSqlPositional).params(dummy1.params()).run()
            throwException()
            query.update(insertSqlPositional).params(dummy2.params()).run()
        });
        then:
        thrown(RollbackException)
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        dummies.size() == 0
    }

    def "Transaction isolation"() {
        when:
        query.transaction().isolation(Transaction.Isolation.READ_COMMITTED).in({ ->
            query.update(insertSqlPositional).params(dummy1.params()).run()
            query.update(insertSqlPositional).params(dummy2.params()).run()
        });
        then:
        List<Dummy> dummies = fluentJdbc.query().select(selectAllSql).listResult(dummyMapper)
        dummies.size() == 2
    }

    protected def "Database inspection with access"() {
        when:
        boolean foundTable = query.databaseInspection().accessMetaData({
            meta ->
                ResultSet rs = meta.getTables(null, null, null, null)
                while (rs.next()) {
                    if ("DUMMY".equals(rs.getString(3))) {
                        return true;
                    }
                }
                return false;
        }
        )
        then:
        foundTable
    }

    protected def "Database inspection with select"() {
        when:
        List<String> tables = query.databaseInspection().selectFromMetaData({
            meta ->
                meta.getTables(null, null, null, null)
        }).listResult({ rs -> return rs.getString(3) })
        then:
        tables.contains("DUMMY")
    }

    protected static void createTestTable(Connection connection) {
        try {
            new FluentJdbcBuilder().build().queryOn(connection).update(dropDummyTable).run()
        } catch(Exception e) {
            // ignorable
        }
        new FluentJdbcBuilder().build().queryOn(connection).update(createDummyTable).run()
    }

    void removeContentAndVerify() {
        query.update("DELETE FROM DUMMY").run()
        def id = fluentJdbc.query().select("SELECT id FROM DUMMY").firstResult(Mappers.singleString())
        assert !id.isPresent()
    }

    void verifyBatchResults(List<Dummy> dummies) {
        assert dummies.size() == 2
        Map<String, Dummy> dummyIndex = Maps.uniqueIndex(dummies, { d -> d.id })
        assert dummyIndex.containsKey(dummy1.id)
        assertDummy(dummyIndex.get(dummy1.id), dummy1)
        assert dummyIndex.containsKey(dummy2.id)
        assertDummy(dummyIndex.get(dummy2.id), dummy2)
    }

    def throwException() {
        throw new RollbackException()
    }

    static class DummyAlias {
        String idAlias
    }
}

class RollbackException extends RuntimeException {

}
