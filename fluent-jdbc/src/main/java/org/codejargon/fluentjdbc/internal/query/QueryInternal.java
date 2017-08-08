package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.*;
import org.codejargon.fluentjdbc.api.query.inspection.DatabaseInspection;
import org.codejargon.fluentjdbc.internal.integration.QueryConnectionReceiverInternal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

public class QueryInternal implements Query {

    final ConnectionProvider connectionProvider;
    final QueryConfig config;
    final PreparedStatementFactory preparedStatementFactory;

    public QueryInternal(
            ConnectionProvider connectionProvider,
            QueryConfig config
    ) {
        this.connectionProvider = connectionProvider;
        this.config = config;
        preparedStatementFactory = new PreparedStatementFactory(config);
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

    @Override
    public Transaction transaction() {
        return new TransactionInternal(this);
    }


    @Override
    public DatabaseInspection databaseInspection() {
        return new DatabaseInspectionInternal(this);
    }

    <T> T query(QueryRunnerConnection<T> runner, Optional<String> sql, SqlErrorHandler sqlErrorHandler) {
        AttemptResult<T> ret = new AttemptResult<>(null, false);
        while(!ret.success) {
            ret = attemptQuery(runner, sql, sqlErrorHandler);
        }
        return ret.result;
    }

    @Override
    public <T> T plainConnection(PlainConnectionQuery<T> plainConnectionQuery) {
        return query(plainConnectionQuery::operation, Optional.empty(), config.defaultSqlErrorHandler.get());
    }

    FluentJdbcException queryException(String sql, Optional<String> reason, Optional<SQLException> e) {
        String message = String.format(
                "Error running query" + (reason.isPresent() ? ": " + reason.get() : "") + ", %s", sql
        );
        return e.isPresent() ? new FluentJdbcSqlException(message, e.get()) : new FluentJdbcException(message);
    }

    private <T> AttemptResult<T> attemptQuery(QueryRunnerConnection<T> runner, Optional<String> sql, SqlErrorHandler sqlErrorHandler) {
        long start = System.currentTimeMillis();
        try {
            T returnValue = doQuery(runner);
            listen(sql, start, Optional.empty());
            return new AttemptResult<>(returnValue, true);
        } catch (SQLException e) {
            handleError(sql, sqlErrorHandler, start, e);
            return new AttemptResult<>(null, false);
        }
    }

    private <T> T doQuery(QueryRunnerConnection<T> runner) throws SQLException {
        QueryConnectionReceiverInternal<T> receiver = new QueryConnectionReceiverInternal<>(runner);
        Optional<Connection> transactionedConnection = TransactionInternal.transactionedConnection(connectionProvider);
        if (!transactionedConnection.isPresent()) {
            connectionProvider.provide(receiver);
        } else {
            receiver.receive(transactionedConnection.get());
        }
        return receiver.returnValue();
    }


    void assignParams(PreparedStatement statement, List<?> params) throws SQLException {
        preparedStatementFactory.assignParams(statement, params);
    }

    private void listen(Optional<String> sql, long start, Optional<SQLException> e) {
        config.afterQueryListener.ifPresent(
                afterQueryListener ->
                        sql.ifPresent(sqlQuery -> afterQueryListener.listen(
                                new ExecutionDetailsInternal(sqlQuery, System.currentTimeMillis() - start, e)
                        ))
        );
    }

    private void handleError(Optional<String> sql, SqlErrorHandler sqlErrorHandler, long start, SQLException e) {
        try {
            checkNotNull(sqlErrorHandler.handle(e, sql), "Action in SqlErrorHandler");
        } catch(SQLException sqle) {
            listen(sql, start, Optional.of(e));
            throw queryException(sql.orElse(""), Optional.empty(), Optional.of(e));
        }
    }

    private static class AttemptResult<T> {
        private final T result;
        private final Boolean success;

        public AttemptResult(T result, Boolean success) {
            this.result = result;
            this.success = success;
        }
    }
}
