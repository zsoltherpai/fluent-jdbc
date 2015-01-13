package org.fluentjdbc.internal.integration;

import org.fluentjdbc.api.integration.QueryConnectionReceiver;
import org.fluentjdbc.internal.query.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryConnectionReceiverInternal<T> implements QueryConnectionReceiver {
    private final QueryRunner<T> runner;
    private T returnValue;

    public QueryConnectionReceiverInternal(QueryRunner<T> runner) {
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
