package org.codejargon.fluentjdbc.internal.query;

import java.sql.SQLException;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;
import org.codejargon.fluentjdbc.api.query.listen.QueryInfo;

public class DefaultSqlHandler implements SqlErrorHandler {
    @Override
    public Action handle(SQLException e, Optional<QueryInfo> queryInfo) throws SQLException {
        throw e;
    }
}
