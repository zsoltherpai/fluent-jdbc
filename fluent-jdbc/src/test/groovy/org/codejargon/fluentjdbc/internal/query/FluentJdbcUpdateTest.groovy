package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.mapper.Mappers
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys

import java.sql.ResultSet
import java.sql.Statement

class FluentJdbcUpdateTest extends UpdateTestBase {
    static def expectedUpdatedRows = 5L

    def "Update with positional parameters"() {
        given:
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        def updateResult = query.update(sql).params(param1, param2).run()
        then:
        updateResult.affectedRows() == expectedUpdatedRows
        interaction {
            verifyQuerying()
        }
    }

    def "Update with named parameters"() {
        given:
        connection.prepareStatement(sql) >> preparedStatement
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        def updateResult = query.update(sqlWithNamedParams).namedParams(namedParams()).run()
        then:
        updateResult.affectedRows() == expectedUpdatedRows
        interaction {
            verifyQuerying()
        }
    }


    def "Update and fetch generated keys"() {
        given:
        Long generatedKey = 5L
        ResultSet genKeyRs = Mock(ResultSet)
        genKeyRs.next() >> true >> false
        genKeyRs.getLong(1) >> generatedKey
        preparedStatement.getGeneratedKeys() >> genKeyRs
        0 * connection.prepareStatement(sql)
        1 * connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) >> preparedStatement
        when:
        UpdateResultGenKeys<Long> result = query.update(sql).params(param1, param2).runFetchGenKeys(Mappers.singleLong());
        then:
        result.generatedKeys().size() == 1
        result.generatedKeys().get(0) == generatedKey
        interaction {
            verifyQuerying()
        }
    }


    def verifyQuerying() {
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
        1 * preparedStatement.close()
    }
}

