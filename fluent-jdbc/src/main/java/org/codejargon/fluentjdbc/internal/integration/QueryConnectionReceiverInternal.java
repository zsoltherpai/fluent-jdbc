package org.codejargon.fluentjdbc.internal.integration;

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
    public void receive(Connection connection) throws SQLException {
        returnValue = runner.run(connection);
    }

    public T returnValue() {
        return returnValue;
    }
}
