package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSqlFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        List<?> flatParameters = params.stream().filter(param -> param instanceof Collection).findFirst().isPresent() ?
                flatten(params) :
                params;
        config.paramAssigner.assignParams(statement, flatParameters);
    }

    @SuppressWarnings("unchecked")
    private List<?> flatten(List<?> params) {
        List flattened = new ArrayList<>();
        params.forEach(
                param -> {
                    if(param instanceof Collection) {
                        flattened.addAll((Collection) param);
                    } else {
                        flattened.add(param);
                    }
                }
        );
        return Collections.unmodifiableList(flattened);
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
