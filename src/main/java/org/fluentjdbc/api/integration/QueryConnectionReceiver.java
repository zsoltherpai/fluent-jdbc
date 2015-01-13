package org.fluentjdbc.api.integration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fluent-Jdbc queries receive the connections through this interface. No need to implement it for integrations.
 */
public interface QueryConnectionReceiver {
    void receive(Connection connection) throws SQLException;
}
