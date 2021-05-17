package org.codejargon.fluentjdbc.internal.query;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.CallableMapper;
import org.codejargon.fluentjdbc.api.query.CallableQuery;
import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;

class CallableQueryInternal extends SingleQueryBase implements CallableQuery {

    CallableQueryInternal(String sql, QueryInternal query) {
        super(query, sql);
    }

    @Override
    public CallableQuery params(List<?> params) {
        addParameters(params);
        return this;
    }

    @Override
    public CallableQuery params(Object... params) {
        addParameters(params);
        return this;
    }

    @Override
    public CallableQuery namedParams(Map<String, ?> namedParams) {
        addNamedParameters(namedParams);
        return this;
    }

    @Override
    public CallableQuery namedParam(String name, Object parameter) {
        addNamedParameter(name, parameter);
        return this;
    }

    @Override
    public CallableQuery errorHandler(SqlErrorHandler sqlErrorHandler ) {
        this.sqlErrorHandler = () -> sqlErrorHandler;
        return this;
    }


    @Override
    public <T> T result(CallableMapper<T> mapper) {
        return runQuery(ps -> {
            if (ps instanceof CallableStatement) {
                CallableStatement cs = (CallableStatement)ps;
                cs.execute();
                return mapper.map(cs);
            } else {
                throw new FluentJdbcException("No out parameters specified for call");
            }
        }, sqlErrorHandler.get());
    }

    @Override
    void customizeQuery(PreparedStatement statement, QueryConfig config) throws SQLException {
        // nothing to do here for callable statements
    }
}
