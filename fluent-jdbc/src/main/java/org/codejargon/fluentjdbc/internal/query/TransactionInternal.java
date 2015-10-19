package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
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
        return !transactionConnection.isPresent() ?
                inNewTransaction(operation, cons) :
                operation.get();
    }

    @Override
    public void inNoResult(Runnable runnable) {
        in(() -> {
            runnable.run();
            return null;
        });
    }

    private <T> T inNewTransaction(Supplier<T> operation, Map<ConnectionProvider, Connection> cons) {
        try {
            List<T> result = new ArrayList<>(1);
            queryInternal.connectionProvider.provide(
                    con -> {
                        Boolean originalAutocommit = null;
                        try {
                            originalAutocommit = con.getAutoCommit();
                            cons.put(queryInternal.connectionProvider, con);
                            try {
                                result.add(operation.get());
                            } catch(Exception e) {
                                con.rollback();
                                throw new FluentJdbcException("Exception while executing transactioned operation. Rolling back.", e);
                            }
                            con.commit();
                        } catch(SQLException e) {
                            throw new FluentJdbcSqlException("Error executing transaction", e);
                        } finally {
                            try {
                                if(originalAutocommit != null && originalAutocommit) {
                                    con.setAutoCommit(true);
                                }
                            } catch(SQLException e) {
                                //
                            }
                            removeTransactionedConnection(cons);
                        }
                    }
            );
            return result.get(0);
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

    static Optional<Connection> transactionedConnection(ConnectionProvider connectionProvider) throws SQLException {
        Optional<Connection> connection = connections.get() != null ?
                Optional.ofNullable(connections.get().get(connectionProvider)) :
                Optional.empty();
        if(connection.isPresent() && connection.get().getAutoCommit()) {
            connection.get().setAutoCommit(false);
        }
        return connection;
    }
}
