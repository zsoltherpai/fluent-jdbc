package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Transaction;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

class TransactionInternal implements Transaction {
    private static ThreadLocal<Map<ConnectionProvider, Connection>> connections = new ThreadLocal<Map<ConnectionProvider, Connection>>() {
        @Override
        protected Map<ConnectionProvider, Connection> initialValue() {
            return null;
        }
    };

    private QueryInternal queryInternal;

    TransactionInternal(QueryInternal queryInternal) {
        this.queryInternal = queryInternal;
    }

    @Override
    public <T> T in(Supplier<T> operation) {
        final ResultHolder<T> resultHolder = new ResultHolder<>();
        Optional<Connection> transactionConnection = transactionedConnection();
        if(!transactionConnection.isPresent()) {
            inNewTransaction(operation, resultHolder);
        } else {
            resultHolder.set(operation.get());
        }
        Preconditions.checkArgument(resultHolder.set, "Internal error: result of transactioned operation not set.");
        return resultHolder.result;
    }

    @Override
    public void inNoResult(Runnable runnable) {
        in(() -> {
            runnable.run();
            return null;
        });
    }

    private <T> void inNewTransaction(Supplier<T> function, ResultHolder<T> resultHolder) {
        try {
            queryInternal.connectionProvider.provide(
                    con -> {
                        try {
                            storeTransactionedConnection(con);
                            con.setAutoCommit(false);
                            try {
                                resultHolder.set(function.get());
                            } catch(Exception e) {
                                con.rollback();
                                throw new FluentJdbcException("Exception while executing transactioned operation. Rolling back.", e);
                            }
                            con.commit();
                            con.setAutoCommit(true);
                        } catch(SQLException e) {
                            throw new FluentJdbcSqlException("Error executing transaction", e);
                        } finally {
                            try {
                                con.setAutoCommit(true);
                            } catch(SQLException e) {
                                //
                            }
                            removeTransactionedConnection();
                        }
                    }
            );
        } catch(SQLException e) {
            // should not occur
            throw new FluentJdbcSqlException("Error executing transaction.", e);
        }
    }

    private Optional<Connection> transactionedConnection() {
        return transactionedConnection(queryInternal.connectionProvider);
    }

    private void storeTransactionedConnection(Connection con) {
        if(connections.get() == null) {
            connections.set(new HashMap<>(4));
        }
        connections.get().put(queryInternal.connectionProvider, con);
    }

    private void removeTransactionedConnection() {
        connections.get().remove(queryInternal.connectionProvider);
        if(connections.get().size() == 0) {
            connections.set(null);
        }
    }

    static Optional<Connection> transactionedConnection(ConnectionProvider connectionProvider) {
        return connections.get() != null ?
                Optional.ofNullable(connections.get().get(connectionProvider)) :
                Optional.empty();
    }

    class ResultHolder<T> {
        private boolean set = false;
        private T result;
        private void set(T res) {
            result = res;
            set = true;
        }

    }
}
