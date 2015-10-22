package org.codejargon.fluentjdbc.api.query;

import java.sql.Connection;
import java.util.function.Supplier;

public interface Transaction {
    /**
     * Specifies transaction isolation
     * @param isolation transaction isolation level - must be supported by the JDBC driver
     * @return this
     */
    Transaction isolation(Isolation isolation);

    /**
     * Runs the function in a transaction, returns a result.
     * @param operation the operation to be executed in a transaction, returns a result
     * @param <T> type of result
     * @return result
     */
    <T> T in(Supplier<T> operation);

    /**
     * Runs the specified Runnable in a transaction. No return result.
     * @param runnable Will be executed in transaction.
     */
    void inNoResult(Runnable runnable);

    public enum Isolation {
        NONE(Connection.TRANSACTION_NONE),
        READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
        READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
        REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
        SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

        private final Integer jdbcIsolation;

        Isolation(Integer jdbcIsolation) {
            this.jdbcIsolation = jdbcIsolation;
        }

        public Integer jdbcIsolation() {
            return jdbcIsolation;
        }
    }
}
