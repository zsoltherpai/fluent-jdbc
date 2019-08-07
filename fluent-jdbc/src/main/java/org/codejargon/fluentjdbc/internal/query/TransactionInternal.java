package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

class TransactionInternal implements Transaction {
    private static ThreadLocal<Map<ConnectionProvider, Connection>> connections = new ThreadLocal<>();

    private final QueryInternal queryInternal;
    private Optional<Isolation> isolation;

    TransactionInternal(QueryInternal queryInternal) {
        this.queryInternal = queryInternal;
        this.isolation = queryInternal.config.defaultTransactionIsolation;
    }

    @Override
    public Transaction isolation(Isolation isolation) {
        this.isolation = Optional.of(isolation);
        return this;
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
                            isolation(con);
                            originalAutocommit = con.getAutoCommit();
                            cons.put(queryInternal.connectionProvider, con);
                            try {
                                result.add(operation.get());
                            } catch(RuntimeException e) {
                                if (!con.getAutoCommit()) {
                                    con.rollback();
                                }
                                throw e;
                            }
                            con.commit();
                        } catch(SQLException e) {
                            throw new FluentJdbcSqlException("Error executing transaction", e);
                        } finally {
                            restoreOriginalAutocommit(con, originalAutocommit);
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

    private void isolation(Connection con) throws SQLException {
        if(isolation.isPresent()) {
            con.setTransactionIsolation(isolation.get().jdbcIsolation());
        }
    }

    private Map<ConnectionProvider, Connection> connections() {
        Map<ConnectionProvider, Connection> cons = connections.get();
        if(cons == null) {
            cons = new ConcurrentHashMap<>(4);
            connections.set(cons);
        }
        return cons;
    }

    private void removeTransactionedConnection(Map<ConnectionProvider, Connection> cons) {
        cons.remove(queryInternal.connectionProvider);
        if(cons.isEmpty()) {
            connections.remove();
        }
    }

    private void restoreOriginalAutocommit(Connection con, Boolean originalAutocommit) {
        try {
            if(originalAutocommit != null && originalAutocommit) {
                con.setAutoCommit(true);
            }
        } catch(SQLException e) {
            //
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
