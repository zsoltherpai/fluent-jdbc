package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbcException
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query
import org.junit.Test
import spock.lang.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*

class FluentJdbcSelectTest  extends Specification {
    static final String sql = "SELECT * FROM BAR"
    static final String column = "FOO"


    static String param1 = "lille"
    static String param2 = "lamb"
    static String result1 = "1"
    static String result2 = "2"
    static String result3 = "3"

    def connection = Mock(Connection)
    def preparedStatement = Mock(PreparedStatement)
    def resultset = Mock(ResultSet)

    Query query;

    def setup() {
        connection.prepareStatement(sql) >> preparedStatement
        query = new FluentJdbcBuilder().connectionProvider(
                { q -> q.receive(connection)}
        ).build().query();
    }

    @Test
    def "Select into list"() {
        given:
        mockSelectData()
        when:
        def dummies = query.select(sql).params(param1, param2).listResult(dummyMapper)
        then:
        assertSelectResult(dummies)
        interaction {
            querying()
        }
    }

    def "Select into list with filtering"() {
        given:
        mockSelectData()
        when:
        def dummies = query
                .select(sql)
                .params(param1, param2)
                .filter({Dummy dummy -> dummy.foo.equals(result1)})
                .listResult(dummyMapper)
        then:
        dummies.size() == 1
        dummies.get(0).foo == result1
        interaction {
            querying()
        }
    }

    def "Select into set"() {
        given:
        mockSelectData()
        when:
        def dummies = query.select(sql).params(param1, param2).setResult(dummyMapper)
        then:
        dummies.size() == 3;
        dummies.stream().allMatch({d -> [result1, result2, result3].contains(d.foo)})
        interaction {
            querying()
        }
    }


    def "Select single"() throws SQLException {
        given:
        mockSelectData()
        when:
        def dummy = query.select(sql).params(param1, param2).singleResult(dummyMapper)
        then:
        dummy.foo == result1
        interaction {
            querying()
        }
    }


    def "Select single with not results"() throws SQLException {
        given:
        mockEmptySelectData()
        when:
        query.select(sql).params(param1, param2).singleResult(dummyMapper)
        then:
        thrown(FluentJdbcException)
    }

    def "Select first"() {
        given:
        mockSelectData();
        when:
        def dummy = query.select(sql).params(param1, param2).firstResult(dummyMapper);
        then:
        dummy.isPresent()
        dummy.get().foo == result1
        interaction {
            querying()
        }
    }

    def "Select first with no result"() {
        given:
        mockEmptySelectData()
        when:
        def dummy = query.select(sql).params(param1, param2).firstResult(dummyMapper)
        then:
        !dummy.isPresent()
        interaction {
            querying()
        }
    }


    def "Select with named parameters"() {
        given:
        def namedParamSql = "SELECT * FROM BAR WHERE COL1 = :param1 AND COL2 = :param2 AND COL3 = :param1"
        def expectedSql = "SELECT * FROM BAR WHERE COL1 = ? AND COL2 = ? AND COL3 = ?"
        connection.prepareStatement(expectedSql) >> preparedStatement // expected?
        mockSelectData()
        def namedParams = ["param1": param1, "param2": param2]
        when:
        query.select(namedParamSql).namedParams(namedParams).firstResult(dummyMapper)
        then:
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
        1 * preparedStatement.setObject(3, param1)
    }

    def "Select with missing named parameters fails"() {
        given:
        String namedParamSql = "SELECT * FROM BAR WHERE COL1 = :param1"
        connection.prepareStatement(_) >> preparedStatement
        mockSelectData()
        def namedParams = [:]
        when:
        query.select(namedParamSql).namedParams(namedParams).firstResult(dummyMapper);
        then:
        thrown(FluentJdbcException)
    }


    def "Select with fetchSize specified"() throws SQLException {
        given:
        def fetchSize = 3
        mockSelectData()
        when:
        def dummies = query.select(sql).params(param1, param2).fetchSize(fetchSize).listResult(dummyMapper)
        then:
        assertSelectResult(dummies)
        interaction {
            querying()
        }
        1 * preparedStatement.setFetchSize(fetchSize)
    }


    def "Select with maxRows specified"() throws SQLException {
        given:
        def maxRows = 3L
        mockSelectData()
        when:
        def dummies = query.select(sql).params(param1, param2).maxRows(maxRows).listResult(dummyMapper)
        then:
        assertSelectResult(dummies)
        interaction {
            querying()
        }
        1 * preparedStatement.setMaxRows(maxRows)
    }

    void assertSelectResult(List<Dummy> dummies) {
        assert dummies.size() == 3
        assert dummies.get(0).foo == result1
        assert dummies.get(1).foo == result2
        assert dummies.get(2).foo == result3
    }

    private void mockSelectData() {
        preparedStatement.executeQuery() >> resultset
        resultset.next() >> true >> true >> true >> false
        resultset.getString(column) >> result1 >> result2 >> result3
    }

    private void mockEmptySelectData() {
        preparedStatement.executeQuery() >> resultset
        resultset.next() >> false
    }


    def querying() {
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
        1 * preparedStatement.close()
    }

    static Mapper<Dummy> dummyMapper = { rs -> new Dummy(rs.getString(column)) }

    static class Dummy {
        final String foo

        Dummy(String foo) {
            this.foo = foo
        }
    }
}
