package org.codejargon.fluentjdbc.internal.query
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider
import org.codejargon.fluentjdbc.api.query.Query
import org.codejargon.fluentjdbc.api.query.listen.ExecutionDetails
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class QueryListenerTest extends Specification {
    protected static final def sql = "UPDATE FOO SET BAR = 'x' WHERE COL1 = ? AND COL2 = ?"
    Query query
    def preparedStatement = Mock(PreparedStatement)
    Connection connection = Mock(Connection)
    ExecutionDetails executionDetails = null

    def setup() {
        connection.prepareStatement(sql) >> preparedStatement
        ConnectionProvider connectionProvider = { q ->
            q.receive(connection)
        }
        query = new FluentJdbcBuilder()
                .connectionProvider(connectionProvider)
                .afterQueryListener({ details -> this.executionDetails = details } )
                .build()
                .query()
    }

    def "Listener invoked"() {
        when:
        query.update(sql).run()
        then:
        executionDetails != null
        executionDetails.success()
        executionDetails.sql() == sql
        executionDetails.executionTimeMs() >= 0
        !executionDetails.sqlException().isPresent()
    }

    def "SQLException propagated by the listener"() {
        given:
        preparedStatement.executeUpdate() >> { throw new SQLException() }
        when:
        query.update(sql).run()
        then:
        thrown(FluentJdbcSqlException)
        executionDetails != null
        !executionDetails.success()
        executionDetails.sql() == sql
        executionDetails.executionTimeMs() >= 0
        executionDetails.sqlException().isPresent()
    }
}
