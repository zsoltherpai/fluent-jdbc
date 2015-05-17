package org.codejargon.fluentjdbc.internal.integration;

import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.QueryConnectionReceiver;
import org.codejargon.fluentjdbc.internal.query.QueryRunnerConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryConnectionReceiverInternal<T> implements QueryConnectionReceiver {
    private final QueryRunnerConnection<T> runner;
    private T returnValue;

    public QueryConnectionReceiverInternal(QueryRunnerConnection<T> runner) {
        this.runner = runner;
    }

    @Override
    public void receive(Connection connection) {
        try {
            returnValue = runner.run(connection);
        } catch (SQLException e) {
            throw new FluentJdbcSqlException("Error while receiving connection", e);
        }
    }

    public T returnValue() {
        return returnValue;
    }
}
