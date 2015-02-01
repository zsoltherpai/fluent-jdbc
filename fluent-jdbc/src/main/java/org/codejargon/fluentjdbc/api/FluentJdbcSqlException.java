package org.codejargon.fluentjdbc.api;

import java.sql.SQLException;

/**
 * A runtime Exception that wraps all SQLExceptions thrown by the underlying JDBC API
 */
public class FluentJdbcSqlException extends FluentJdbcException {
    public FluentJdbcSqlException(String message, SQLException sqlException) {
        super(message, sqlException);
    }
    
    public SQLException sqlException() {
        return (SQLException) getCause();
    }
}
