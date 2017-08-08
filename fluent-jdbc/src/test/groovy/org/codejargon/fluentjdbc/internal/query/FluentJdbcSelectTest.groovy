package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbcException
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query
import org.junit.Test
import spock.lang.Specification

import javax.naming.OperationNotSupportedException;
import java.sql.Connection
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException
import java.sql.Types;
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
        connection.prepareStatement(expectedSql) >> preparedStatement
        mockSelectData()
        def namedParams = ["param1": param1, "param2": param2]
        when:
        query.select(namedParamSql).namedParams(namedParams).firstResult(dummyMapper)
        then:
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
        1 * preparedStatement.setObject(3, param1)
    }

    def "Select with named parameters including Collections"() {
        given:
        def namedParamSql = "SELECT * FROM BAR WHERE COL in (:params)"
        def expectedSql1 = "SELECT * FROM BAR WHERE COL in (?, ?)"
        def expectedSql2 = "SELECT * FROM BAR WHERE COL in (?, ?, ?)"
        connection.prepareStatement(expectedSql1) >> preparedStatement
        connection.prepareStatement(expectedSql2) >> preparedStatement
        mockSelectData()
        def namedParams1 = ["params": [null, param2]]
        def namedParams2 = ["params": [param1, param2, "foo"]]
        when:
        query.select(namedParamSql).namedParams(namedParams1).firstResult(dummyMapper)
        then:
        1 * preparedStatement.setNull(1, Types.VARCHAR)
        1 * preparedStatement.setObject(2, param2)
        0 * preparedStatement.setObject(3, _)
        when:
        query.select(namedParamSql).namedParams(namedParams2).firstResult(dummyMapper)
        then:
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
        1 * preparedStatement.setObject(3, "foo")
        when:
        query.select(namedParamSql).namedParams(namedParams1).firstResult(dummyMapper)
        then:
        1 * preparedStatement.setNull(1, Types.VARCHAR)
        1 * preparedStatement.setObject(2, param2)
        0 * preparedStatement.setObject(3, _)
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

    def "Select with empty named parameters"() {
        given:
        String namedParamSql = "SELECT * FROM BAR"
        connection.prepareStatement(_) >> preparedStatement
        mockSelectData()
        def namedParams = [:]
        when:
        query.select(namedParamSql).namedParams(namedParams).firstResult(dummyMapper);
        then:
        true
    }

    def "Select with custom error handler"() {
        given:
        String namedParamSql = "SELECT * FROM BAR"
        connection.prepareStatement(_) >> preparedStatement
        preparedStatement.executeQuery() >> {throw new SQLException ("Oops")}
        when:
        query.select(namedParamSql).errorHandler({e, sql -> throw new OhNoes(namedParamSql)}).firstResult(dummyMapper);
        then:
        def e = thrown(OhNoes)
        e.getMessage() == namedParamSql
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
        ParameterMetaData parameterMetaData = Mock(ParameterMetaData)
        preparedStatement.getParameterMetaData() >> parameterMetaData
        parameterMetaData.getParameterType(_) >> Types.VARCHAR
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

    static class OhNoes extends RuntimeException {
        OhNoes(String msg) {
            super(msg)
        }
    }
}
