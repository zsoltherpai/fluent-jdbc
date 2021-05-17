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
    protected static final def namedSql = "UPDATE FOO SET BAR = 'x' WHERE COL1 = :name AND COL2 = :name"
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

    def "Listener invoked without params"() {
        when:
        query.update(sql).run()
        then:
        executionDetails != null
        executionDetails.success()
        executionDetails.queryInfo().sql() == sql
        !executionDetails.queryInfo().params().isPresent()
        !executionDetails.queryInfo().namedParams().isPresent()
        executionDetails.executionTimeMs() >= 0
        !executionDetails.sqlException().isPresent()
    }
    
    def "Listener invoked with param"() {
        when:
        query.update(sql).params(5).run()
        then:
        executionDetails != null
        executionDetails.success()
        executionDetails.queryInfo().sql() == sql
        executionDetails.queryInfo().params().isPresent()
        !executionDetails.queryInfo().namedParams().isPresent()
        executionDetails.executionTimeMs() >= 0
        !executionDetails.sqlException().isPresent()
    }

    def "Listener invoked with named param"() {
        when:
        query.update(namedSql).namedParam("name", "param").run()
        then:
        executionDetails != null
        executionDetails.success()
        executionDetails.queryInfo().sql() == namedSql
        !executionDetails.queryInfo().params().isPresent()
        executionDetails.queryInfo().namedParams().isPresent()
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
        executionDetails.queryInfo().sql() == sql
        executionDetails.executionTimeMs() >= 0
        executionDetails.sqlException().isPresent()
    }
}
