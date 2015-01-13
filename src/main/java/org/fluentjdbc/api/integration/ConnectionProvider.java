package org.fluentjdbc.api.integration;

import java.sql.SQLException;

/**
 * API for providing Connections to Fluent-Jdbc queries. Note that the API allows both acquiring and releasing a Connection.
 *
 * This makes it possible to integrate FluentJdbc to most transaction management solutions.
 *
 *
 * See for example the implementation of DataSourceConnectionProvider (for managed DataSources):
 *
 * query -> {
 *      try(Connection connection = dataSource.getConnection()) {
 *          query.receive(connection);
 *      }
 *   }
 *
 *
 * Or integrating with Spring through JdbcOperations/JdbcTemplate:
 *
 * query -> {
 *     jdbcOperations.execute(connection -> {
 *        query.receive(connection);
 *     });
 * }
 *
 */
public interface ConnectionProvider {
    void provide(QueryConnectionReceiver query) throws SQLException;
}
