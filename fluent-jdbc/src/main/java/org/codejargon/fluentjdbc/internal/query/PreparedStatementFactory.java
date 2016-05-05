package org.codejargon.fluentjdbc.internal.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

class PreparedStatementFactory {
    public static final String[] emptyGenColumns = new String[]{};
    private final QueryConfig config;

    PreparedStatementFactory(QueryConfig config) {
        this.config = config;
    }

    PreparedStatement createSingle(Connection con, SingleQueryBase singleQueryBase, boolean fetchGenerated, String[] genColumns) throws SQLException {
        SqlAndParams sqlAndParams = singleQueryBase.sqlAndParams(config);
        PreparedStatement statement = prepareStatement(con, sqlAndParams.sql(), fetchGenerated, genColumns);
        singleQueryBase.customizeQuery(statement, config);
        assignParams(statement, sqlAndParams.params());
        return statement;
    }

    PreparedStatement createBatch(Connection con, String sql) throws SQLException {
        return prepareStatement(con, sql, false, emptyGenColumns);
    }

    void assignParams(PreparedStatement statement, List<?> params) throws SQLException {
        config.paramAssigner.assignParams(statement, params);
    }

    private PreparedStatement prepareStatement(Connection con, String sql, Boolean fetchGenerated, String[] genColumns) throws SQLException {
        return fetchGenerated ?
                (genColumns.length > 0 ?
                        con.prepareStatement(sql, genColumns) :
                        con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                ) :
                con.prepareStatement(sql);
    }




}
