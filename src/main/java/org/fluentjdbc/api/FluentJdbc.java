package org.fluentjdbc.api;

import java.sql.Connection;
import org.fluentjdbc.api.query.Query;

/**
 * Provides fluent Query API
 */
public interface FluentJdbc {
    /**
     * Query on a connection provided by ConnectionProvider. Fails if no ConnectionProvider is set.
     */
    Query query();

    /**
     * Query on the given managed connection.
     */
    Query queryOn(Connection connection);
}
