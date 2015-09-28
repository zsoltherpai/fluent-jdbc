package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider
import org.codejargon.fluentjdbc.api.query.Query
import org.junit.Before
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

public abstract class UpdateTestBase extends Specification {
    protected static final def sql = "UPDATE FOO SET BAR = 'x' WHERE COL1 = ? AND COL2 = ?"
    protected static final def sqlWithNamedParams = "UPDATE FOO SET BAR = 'x' WHERE COL1 = :param1 AND COL2 = :param2"
    protected static final def param1 = "lille"
    protected static final def param2 = "lamb"

    protected def connection = Mock(Connection)
    protected def preparedStatement = Mock(PreparedStatement)
    protected ConnectionProvider connectionProvider
    protected def connectionProvided = 0;

    protected FluentJdbc fluentJdbc
    protected Query query

    @Before
    def setupBase() throws SQLException {
        connection.prepareStatement(sql) >> preparedStatement
        connectionProvider = { q ->
            connectionProvided++
            q.receive(connection)
        }
        fluentJdbc = new FluentJdbcBuilder().connectionProvider(connectionProvider).build()
        query = fluentJdbc.query()
    }

    protected Map<String, ?> namedParams() {
        return ["param1": param1, "param2": param2]
    }
}
