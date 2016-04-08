package org.codejargon.fluentjdbc.internal.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

class PreparedStatementFactory {
    private final QueryConfig config;

    PreparedStatementFactory(QueryConfig config) {
        this.config = config;
    }

    PreparedStatement createSingle(Connection con, SingleQueryBase singleQueryBase, boolean fetchGenerated) throws SQLException {
        SqlAndParams sqlAndParams = singleQueryBase.sqlAndParams(config);
        PreparedStatement statement = prepareStatement(con, sqlAndParams.sql(), fetchGenerated);
        singleQueryBase.customizeQuery(statement, config);
        assignParams(statement, sqlAndParams.params());
        return statement;
    }

    PreparedStatement createBatch(Connection con, String sql) throws SQLException {
        return prepareStatement(con, sql, false);
    }

    void assignParams(PreparedStatement statement, Collection<?> params) throws SQLException {
        config.paramAssigner.assignParams(statement, params);
    }

    private PreparedStatement prepareStatement(Connection con, String sql, Boolean fetchGenerated) throws SQLException {
        return fetchGenerated ?
                con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) :
                con.prepareStatement(sql);
    }


}
