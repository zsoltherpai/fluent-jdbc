package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

class TransactionInternal implements Transaction {
    private static ThreadLocal<Map<ConnectionProvider, Connection>> connections = new ThreadLocal<>();

    private QueryInternal queryInternal;

    TransactionInternal(QueryInternal queryInternal) {
        this.queryInternal = queryInternal;
    }

    @Override
    public <T> T in(Supplier<T> operation) {
        Map<ConnectionProvider, Connection> cons = connections();
        Optional<Connection> transactionConnection = Optional.ofNullable(cons.get(queryInternal.connectionProvider));
        final ResultHolder<T> resultHolder = new ResultHolder<>();
        if(!transactionConnection.isPresent()) {
            inNewTransaction(operation, resultHolder, cons);
        } else {
            resultHolder.set(operation.get());
        }
        return resultHolder.get();
    }

    @Override
    public void inNoResult(Runnable runnable) {
        in(() -> {
            runnable.run();
            return null;
        });
    }

    private <T> void inNewTransaction(Supplier<T> function, ResultHolder<T> resultHolder, Map<ConnectionProvider, Connection> cons) {
        try {
            queryInternal.connectionProvider.provide(
                    con -> {
                        try {
                            cons.put(queryInternal.connectionProvider, con);
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
                            removeTransactionedConnection(cons);
                        }
                    }
            );
        } catch(SQLException e) {
            // should not occur
            throw new FluentJdbcSqlException("Error executing transaction.", e);
        }
    }

    private Map<ConnectionProvider, Connection> connections() {
        Map<ConnectionProvider, Connection> cons = connections.get();
        if(cons == null) {
            cons = new HashMap<>(4);
            connections.set(cons);
        }
        return cons;
    }

    private void removeTransactionedConnection(Map<ConnectionProvider, Connection> cons) {
        cons.remove(queryInternal.connectionProvider);
        if(cons.size() == 0) {
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
        private T get() {
            if(!set) {
                throw new FluentJdbcException("Transactioned operation result not set");
            }
            return result;
        }
    }
}
