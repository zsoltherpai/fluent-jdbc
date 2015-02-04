package org.codejargon.fluentjdbc.api;

import java.sql.Connection;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Query;

/**
 * Creates fluent Query API baded on FluentJdbc's configuration. Thread-safe.
 * 
 * @see org.codejargon.fluentjdbc.api.FluentJdbcBuilder
 */
public interface FluentJdbc {
    /**
     * Creates a Query API on a connection provided by the ConnectionProvider. Fails if no ConnectionProvider is set.
     * @return Query API on a connection provided by the ConnectionProvider
     */
    Query query();

    /**
     * Creates a Query API using a given managed connection.
     *
     * @param connection managed sql Connection
     * @return Query API for the given connection
     */
    Query queryOn(Connection connection);
}
