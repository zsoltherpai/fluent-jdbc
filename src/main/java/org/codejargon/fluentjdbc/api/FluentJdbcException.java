package org.codejargon.fluentjdbc.api;

/**
 * Base exception for exceptions thrown by Fluent-Jdbc
 */
public class FluentJdbcException extends RuntimeException {
    public FluentJdbcException(String message) {
        super(message);
    }

    public FluentJdbcException(String message, Throwable cause) {
        super(message, cause);
    }
}
