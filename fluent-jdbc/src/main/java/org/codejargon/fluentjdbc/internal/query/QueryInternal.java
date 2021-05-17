package org.codejargon.fluentjdbc.internal.query;

import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.api.query.CallableQuery;
import org.codejargon.fluentjdbc.api.query.PlainConnectionQuery;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;
import org.codejargon.fluentjdbc.api.query.Transaction;
import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.api.query.inspection.DatabaseInspection;
import org.codejargon.fluentjdbc.api.query.listen.QueryInfo;
import org.codejargon.fluentjdbc.internal.integration.QueryConnectionReceiverInternal;

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
    public CallableQuery call(String sql) {
        return new CallableQueryInternal(sql, this);
    }
    
    @Override
    public Transaction transaction() {
        return new TransactionInternal(this);
    }


    @Override
    public DatabaseInspection databaseInspection() {
        return new DatabaseInspectionInternal(this);
    }

    <T> T query(QueryRunnerConnection<T> runner, Optional<QueryInfo> queryInfo, SqlErrorHandler sqlErrorHandler) {
        AttemptResult<T> ret = new AttemptResult<>(null, false);
        while(!ret.success) {
            ret = attemptQuery(runner, queryInfo, sqlErrorHandler);
        }
        return ret.result;
    }

    @Override
    public <T> T plainConnection(PlainConnectionQuery<T> plainConnectionQuery) {
        return query(plainConnectionQuery::operation, Optional.empty(), config.defaultSqlErrorHandler.get());
    }

    FluentJdbcException queryException(Optional<QueryInfo> queryInfo, Optional<String> reason, Optional<SQLException> e) {
        String sql = queryInfo.map(QueryInfo::sql).orElse("");
        String message = reason.isPresent()
                ? String.format("Error running query: %s, %s", reason.get(), sql)
                : String.format("Error running query, %s", sql);
        return e.isPresent() ? new FluentJdbcSqlException(message, e.get()) : new FluentJdbcException(message);
    }

    private <T> AttemptResult<T> attemptQuery(QueryRunnerConnection<T> runner, Optional<QueryInfo> queryInfo, SqlErrorHandler sqlErrorHandler) {
        long start = System.currentTimeMillis();
        try {
            T returnValue = doQuery(runner);
            listen(queryInfo, start, Optional.empty());
            return new AttemptResult<>(returnValue, true);
        } catch (SQLException e) {
            handleError(queryInfo, sqlErrorHandler, start, e);
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

    private void listen(Optional<QueryInfo> queryInfo, long start, Optional<SQLException> e) {
        config.afterQueryListener.ifPresent(
                afterQueryListener ->
                        queryInfo.ifPresent(sqlQueryInfo -> afterQueryListener.listen(
                                new ExecutionDetailsInternal(sqlQueryInfo, System.currentTimeMillis() - start, e)
                        ))
        );
    }

    private void handleError(Optional<QueryInfo> queryInfo, SqlErrorHandler sqlErrorHandler, long start, SQLException e) {
        try {
            checkNotNull(sqlErrorHandler.handle(e, queryInfo), "Action in SqlErrorHandler");
        } catch(SQLException sqle) {
            listen(queryInfo, start, Optional.of(e));
            throw queryException(queryInfo, Optional.empty(), Optional.of(e));
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
