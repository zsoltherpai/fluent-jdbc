package org.codejargon.fluentjdbc.api.integration;

import java.sql.SQLException;

/**
 * <p>
 * API for providing Connections to Fluent-Jdbc queries. Note that the API allows both acquiring and
 * releasing a Connection. This makes it possible to integrate FluentJdbc to most pooling / transaction
 * management solutions.
 * </p>
 *
 * <p>
 * Example implementations:
 * </p>
 *
 * <p>
 * Getting connection from a datasource (provided in the library as DataSourceConnectionProvider):
 * </p>
 *
 * <pre>
 * query -&gt; {
 *      try(Connection connection = dataSource.getConnection()) {
 *          query.receive(connection);
 *      }
 *   }
 * </pre>
 *
 * <p>
 * Getting connection from a callback mechanism (eg Spring JdbcOperations/JdbcTemplate):
 * </p>
 *
 * <pre>
 * query -&gt; {
 *     jdbcOperations.execute(connection -&gt; {
 *        query.receive(connection);
 *     });
 * }
 * </pre>
 *
 */
public interface ConnectionProvider {
    void provide(QueryConnectionReceiver query) throws SQLException;
}
