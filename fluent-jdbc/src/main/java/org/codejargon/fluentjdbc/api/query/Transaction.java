package org.codejargon.fluentjdbc.api.query;

import java.util.function.Supplier;

public interface Transaction {
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
}
