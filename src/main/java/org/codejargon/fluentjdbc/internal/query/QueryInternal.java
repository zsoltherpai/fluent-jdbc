package org.codejargon.fluentjdbc.internal.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;
import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.internal.integration.QueryConnectionReceiverInternal;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedSqlAndParams;
import org.codejargon.fluentjdbc.internal.query.namedparameter.TransformedSql;

public class QueryInternal implements Query {

    final ConnectionProvider connectionProvider;
    private final ParamAssigner paramAssigner;
    private final Map<String, TransformedSql> namedParamSqlCache;


    public QueryInternal(ConnectionProvider connectionProvider, ParamAssigner paramAssigner, Map<String, TransformedSql> namedParamSqlCache) {
        this.connectionProvider = connectionProvider;
        this.paramAssigner = paramAssigner;
        this.namedParamSqlCache = namedParamSqlCache;
    }

    @Override
    public SelectQuery select(String sql) {
        return new SelectQueryInternal(sql, this);
    }

    @Override
    public UpdateQuery update(String sql) {
        return new UpdateQueryInternal(sql, this);
    }

    @Override
    public BatchQuery batch(String sql) {
        return new BatchQueryInternal(sql, this);
    }

    <T> T query(QueryRunner<T> runner, String sql) {
        try {
            QueryConnectionReceiverInternal<T> receiver = new QueryConnectionReceiverInternal<>(runner);
            connectionProvider.provide(receiver);
            return receiver.returnValue();
        } catch(SQLException e) {
            throw queryException(sql, Optional.empty(), Optional.of(e));
        }
    }

    FluentJdbcException queryException(String sql, Optional<String> reason, Optional<SQLException> e) {
        String message = String.format("Error running query" + (reason.isPresent() ? ": " + reason.get() : "") + ", %s", sql);
        return e.isPresent() ? new FluentJdbcSqlException(message, e.get()) : new FluentJdbcException(message);
    }

    PreparedStatement preparedStatement(Connection con, String sql, List<Object> params, Map<String, Object> namedParams) throws SQLException {
        SqlAndParams sqlAndParams = namedParams.isEmpty() ?
                new SqlAndParams(sql, params) :
                NamedSqlAndParams.sqlAndParams(transformedSql(sql), namedParams);
        PreparedStatement statement = con.prepareStatement(sqlAndParams.sql());
        assignParams(statement, sqlAndParams.params());
        return statement;
    }

    void assignParams(PreparedStatement statement, List<Object> params) throws SQLException {
        paramAssigner.assignParams(statement, params);
    }

    TransformedSql transformedSql(String sql) {
        if(!namedParamSqlCache.containsKey(sql)) {
            namedParamSqlCache.put(sql, TransformedSql.forSql(sql));
        }
        return namedParamSqlCache.get(sql);
    }
}
