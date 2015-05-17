package org.codejargon.fluentjdbc.api.integration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fluent-Jdbc queries receive the connections from this interface. Should be called in ConnectionProvider implementations,
 * no need to implement it for integrations.
 */
@FunctionalInterface
public interface QueryConnectionReceiver {
    void receive(Connection connection);
}
