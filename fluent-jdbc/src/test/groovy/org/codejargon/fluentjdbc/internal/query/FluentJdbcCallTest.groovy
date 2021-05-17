package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbcException
import org.codejargon.fluentjdbc.api.query.CallableMapper;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query
import org.junit.Test
import spock.lang.Specification

import javax.naming.OperationNotSupportedException;
import java.sql.Connection
import java.sql.ParameterMetaData;
import java.sql.CallableStatement;
import java.sql.SQLException
import java.sql.Types;
import java.util.*

class FluentJdbcCallTest  extends Specification {

    static String result1 = "res"
    static String param1 = "lille"
    static String param2 = "lamb"

    def connection = Mock(Connection)
    def callableStatement = Mock(CallableStatement)

    Query query;

    def setup() {
        query = new FluentJdbcBuilder().connectionProvider(
                { q -> q.receive(connection)}
        ).build().query();
    }

    def "Call with ordered parameters"() {
        given:
        def sql = "{? = call pack.func(?, ?)}"
        connection.prepareCall(sql) >> callableStatement
        mockSelectData()
        def orderedParams = [java.sql.JDBCType.VARCHAR, param1, param2]
        when:
        query.call(sql).params(orderedParams).result(dummyMapper)
        then:
        1 * callableStatement.registerOutParameter(1, java.sql.JDBCType.VARCHAR)
        1 * callableStatement.setObject(2, param1)
        1 * callableStatement.setObject(3, param2)
    }

    def "Call with named parameters"() {
        given:
        def namedParamSql = "{:outPar = call pack.func(:param1, :param2, :param1)}"
        def expectedSql = "{? = call pack.func(?, ?, ?)}"
        connection.prepareCall(expectedSql) >> callableStatement
        mockSelectData()
        def namedParams = ["param1": param1, "outPar": java.sql.JDBCType.VARCHAR, "param2": param2]
        when:
        query.call(namedParamSql).namedParams(namedParams).result(dummyMapper)
        then:
        1 * callableStatement.registerOutParameter(1, java.sql.JDBCType.VARCHAR)
        1 * callableStatement.setObject(2, param1)
        1 * callableStatement.setObject(3, param2)
        1 * callableStatement.setObject(4, param1)
    }

    private void mockSelectData() {
        callableStatement.getString(1) >> result1
    }

    static CallableMapper<Dummy> dummyMapper = { cs -> new Dummy(cs.getString(1)) }

    static class Dummy {
        final String foo

        Dummy(String foo) {
            this.foo = foo
        }
    }
}
