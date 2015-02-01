package org.codejargon.fluentjdbc.internal.integration;

import org.codejargon.fluentjdbc.api.integration.QueryConnectionReceiver;
import org.codejargon.fluentjdbc.internal.query.QueryRunner;

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
