package org.codejargon.fluentjdbc.api;

import java.sql.SQLException;

public class FluentJdbcSqlException extends FluentJdbcException {
    public FluentJdbcSqlException(String message, SQLException sqlException) {
        super(message, sqlException);
    }
    
    public SQLException sqlException() {
        return (SQLException) getCause();
    }
}
