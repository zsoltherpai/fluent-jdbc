package org.codejargon.fluentjdbc.api.integration;

import java.sql.SQLException;

/**
 * <p>
 * Provides Connections to FluentJdbc Queries. Note that the API allows both acquiring and
 * releasing a Connection. This makes it possible to integrate FluentJdbc to most pooling / transaction
 * management solutions.
 * </p>
 * 
 * <p>Implementation pattern</p>
 * <pre>
 * query -&gt; {
 *     Connection connection = ... // acquire a connection 
 *     query.receive(connection)   // make the connection available to FluentJdbc queries
 *     connection.close()          // release connection - may not be needed if connection is already managed
 * }         
 * </pre>
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
 *     jdbcTemplate.execute(connection -&gt; {
 *        query.receive(connection);
 *     });
 * }
 * </pre>
 *
 */
@FunctionalInterface
public interface ConnectionProvider {
    void provide(QueryConnectionReceiver query) throws SQLException;
}
