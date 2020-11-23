package org.codejargon.fluentjdbc.internal.query;

import java.sql.SQLException;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;

public class DefaultSqlHandler implements SqlErrorHandler {
    @Override
    public Action handle(SQLException e, Optional<QueryInfoInternal> queryInfo) throws SQLException {
        throw e;
    }
}
