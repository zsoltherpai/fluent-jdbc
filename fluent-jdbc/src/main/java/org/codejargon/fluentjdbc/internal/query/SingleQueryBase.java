package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.query.namedparameter.SqlAndParamsForNamed;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

abstract class SingleQueryBase {
    protected final String sql;
    protected final QueryInternal query;
    protected final List<Object> params = new ArrayList<>(0);
    protected final Map<String, Object> namedParams = new HashMap<>(0);

    protected SingleQueryBase(QueryInternal query, String sql) {
        this.query = query;
        this.sql = sql;
    }

    protected <C extends Collection<?>> void addParameters(C params) {
        Preconditions.checkArgument(namedParams.isEmpty(), "Can not add positional parameters if named parameters are set.");
        this.params.addAll(params);
    }

    protected void addParameters(Object... params) {
        addParameters(Arrays.asList(params));
    }

    protected void addNamedParameters(Map<String, ?> namedParams) {
        Preconditions.checkArgument(params.isEmpty(), "Can not add named parameters if positional parameters are set.");
        this.namedParams.putAll(namedParams);
    }

    protected void addNamedParameter(String name, Object parameter) {
        this.namedParams.put(name, parameter);
    }

    protected <T> T runQuery(
            QueryRunnerPreparedStatement<T> queryRunnerPreparedStatement) {
        return runQuery(queryRunnerPreparedStatement, false);
    }

    protected <T> T runQueryAndFetch(
            QueryRunnerPreparedStatement<T> queryRunnerPreparedStatement) {
        return runQuery(queryRunnerPreparedStatement, true);
    }

    private <T> T runQuery(
            QueryRunnerPreparedStatement<T> queryRunnerPreparedStatement,
            boolean fetchGenerated) {
        return query.query(connection -> {
            try (PreparedStatement ps = query.preparedStatementFactory.createSingle(connection, this, fetchGenerated)) {
                return queryRunnerPreparedStatement.run(ps);
            }
        }, Optional.of(sql));
    }

    SqlAndParams sqlAndParams(QueryConfig config) {
        return namedParams.isEmpty() ?
                new SqlAndParams(sql, params) :
                SqlAndParamsForNamed.create(config.namedTransformedSql(sql), namedParams);
    }

    abstract void customizeQuery(PreparedStatement preparedStatement, QueryConfig config) throws SQLException;
}
