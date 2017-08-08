package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;

import java.sql.SQLException;
import java.util.Optional;

public class DefaultSqlHandler implements SqlErrorHandler {
    @Override
    public Action handle(SQLException e, Optional<String> sql) throws SQLException {
        throw e;
    }
}
