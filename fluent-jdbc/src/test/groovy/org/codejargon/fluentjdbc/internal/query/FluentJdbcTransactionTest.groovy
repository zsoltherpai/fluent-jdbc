package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbcException
import org.codejargon.fluentjdbc.api.query.Transaction

import java.sql.Connection

class FluentJdbcTransactionTest extends UpdateTestBase {
    def expectedUpdatedRows = 5L

    def "No transaction uses different connections"() {
        given:
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        query.update(sql).params(param1, param2).run()
        query.update(sql).params(param1, param2).run()
        query.update(sql).params(param1, param2).run()
        then:
        connectionProvided == 3
        0 * connection.setAutoCommit(false)
        3 * preparedStatement.setObject(1, param1)
        3 * preparedStatement.setObject(2, param2)
        3 * preparedStatement.close()
        0 * connection.commit()
        0 * connection.rollback()
    }

    def "Successful transaction committed, same connection used"() {
        given:
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        def updateResult = query.transaction().in(
                { ->
                    query.update(sql).params(param1, param2).run()
                    query.update(sql).params(param1, param2).run()
                    query.update(sql).params(param1, param2).run()
                }
        );
        then:
        // checking original state, then check on first query
        connection.getAutoCommit() >> true >> true >> false
        connectionProvided == 1
        _ * connection.getAutoCommit();
        1 * connection.setAutoCommit(false)
        3 * preparedStatement.setObject(1, param1)
        3 * preparedStatement.setObject(2, param2)
        3 * preparedStatement.close()
        1 * connection.commit()
        0 * connection.rollback()
        1 * connection.setAutoCommit(true)
        updateResult.affectedRows() == expectedUpdatedRows
    }

    def "Failed operation rolls back transaction"() {
        given:
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        query.transaction().in(
                { ->
                    query.update(sql).params(param1, param2).run()
                    query.update(sql).params(param1, param2).run()
                    throwException()
                    query.update(sql).params(param1, param2).run()
                }
        );
        then:
        // checking original state, then check on first query
        connection.getAutoCommit() >> true >> true >> false

        thrown(MyRuntimeException)
        _ * connection.getAutoCommit();
        1 * connection.setAutoCommit(false)
        2 * preparedStatement.setObject(1, param1)
        2 * preparedStatement.setObject(2, param2)
        2 * preparedStatement.close()
        1 * connection.rollback()
        0 * connection.commit()
        1 * connection.setAutoCommit(true);
    }

    def "Transactions are lazy"() {
        when:
        query.transaction().inNoResult({ -> /* not using query */ });
        then:
        connectionProvided == 0
    }
    
    def "Nested transactions are lazy"() {
        when:
        query.transaction().inNoResult({ -> query.transaction().inNoResult({ -> /* not using query */ })});
        then:
        connectionProvided == 0
    }

    def "Transaction isolation"() {
        given:
        preparedStatement.executeUpdate() >> expectedUpdatedRows.intValue()
        when:
        def updateResult = query.transaction().isolation(Transaction.Isolation.REPEATABLE_READ).in(
                { ->
                    query.update(sql).params(param1, param2).run()
                    query.update(sql).params(param1, param2).run()
                    query.update(sql).params(param1, param2).run()
                }
        );
        then:
        // checking original state, then check on first query
        connection.getAutoCommit() >> true >> true >> false
        connectionProvided == 1
        _ * connection.getAutoCommit()
        1 * connection.setAutoCommit(false)
        1 * connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ)
        1 * connection.commit()
        updateResult.affectedRows() == expectedUpdatedRows
    }

    def throwException() {
        throw new MyRuntimeException()
    }
}

class MyRuntimeException extends RuntimeException {

}
