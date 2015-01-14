package org.fluentjdbc.api;

import java.sql.Connection;
import org.fluentjdbc.api.query.Query;

/**
 * Provides fluent Query API.
 */
public interface FluentJdbc {
    /**
     * Creates a Query on a connection provided by the ConnectionProvider. Fails if no ConnectionProvider is set.
     *
     * @return Query API on a connection provided by the ConnectionProvider
     */
    Query query();

    /**
     * Creates a Query on a given managed connection.
     *
     * @param connection managed sql Connection
     * @return Query API for the given connection
     */
    Query queryOn(Connection connection);
}
