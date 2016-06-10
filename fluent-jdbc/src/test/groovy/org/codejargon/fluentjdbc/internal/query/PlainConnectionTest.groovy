package org.codejargon.fluentjdbc.internal.query
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.query.Query
import org.junit.Test
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

public class PlainConnectionTest extends Specification {
    static final String sql = "SELECT * FROM BAR"
    static final String column = "FOO"


    static String param1 = "lille"
    static String param2 = "lamb"
    static String result1 = "1"
    static String result2 = "2"
    static String result3 = "3"

    def connection = Mock(Connection)
    def preparedStatement = Mock(PreparedStatement)

    Query query;

    def setup() {
        connection.prepareStatement(sql) >> preparedStatement
        query = new FluentJdbcBuilder().connectionProvider(
                { q -> q.receive(connection)}
        ).build().query();
    }

    @Test
    def "Access to connection"() {
        when:
        query.plainConnection({connection ->
            def statement = connection.prepareStatement(sql)
            statement.execute()
        })
        then:
        1 * preparedStatement.execute()
    }
}
