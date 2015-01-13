package org.fluentjdbc.internal.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.fluentjdbc.api.FluentJdbcException;
import org.fluentjdbc.api.FluentJdbcSqlException;
import org.fluentjdbc.api.integration.ConnectionProvider;
import org.fluentjdbc.api.query.*;
import org.fluentjdbc.internal.integration.QueryConnectionReceiverInternal;

public class QueryInternal implements Query {

    final ConnectionProvider connectionProvider;
    private final ParamAssigner paramAssigner;

    public QueryInternal(ConnectionProvider connectionProvider, ParamAssigner paramAssigner) {
        this.connectionProvider = connectionProvider;
        this.paramAssigner = paramAssigner;
    }

    @Override
    public SelectQuery select(String sql) {
        return new SelectQueryInternal(sql, this);
    }

    @Override
    public UpdateQuery update(String sql) {
        return new UpdateQueryInternal(sql, this);
    }

    @Override
    public BatchQuery batch(String sql) {
        return new BatchQueryInternal(sql, this);
    }



    <T> T query(QueryRunner<T> runner, String sql) {
        try {
            QueryConnectionReceiverInternal<T> receiver = new QueryConnectionReceiverInternal<>(runner);
            connectionProvider.provide(receiver);
            return receiver.returnValue();
        } catch(SQLException e) {
            throw queryException(sql, Optional.empty(), Optional.of(e));
        }
    }

    FluentJdbcException queryException(String sql, Optional<String> reason, Optional<SQLException> e) {
        String message = String.format("Error running query" + (reason.isPresent() ? ": " + reason.get() : "") + ", %s", sql);
        return e.isPresent() ? new FluentJdbcSqlException(message, e.get()) : new FluentJdbcException(message);
    }

    PreparedStatement preparedStatement(Connection con, String sql, List<Object> params) throws SQLException {
        PreparedStatement statement = con.prepareStatement(sql);
        assignParams(statement, params);
        return statement;
    }

    void assignParams(PreparedStatement statement, List<Object> params) throws SQLException {
        paramAssigner.assignParams(statement, params);
    }
}
