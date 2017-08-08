package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class UpdateQueryInternal extends SingleQueryBase implements UpdateQuery {

    UpdateQueryInternal(String sql, QueryInternal query) {
        super(query, sql);
    }

    @Override
    public UpdateQuery errorHandler(SqlErrorHandler sqlErrorHandler ) {
        this.sqlErrorHandler = sqlErrorHandler;
        return this;
    }


    @Override
    public UpdateResult run() {
        return runQuery(
                ps -> new UpdateResultInternal((long) ps.executeUpdate()),
                sqlErrorHandler
        );
    }

    @Override
    public <T> UpdateResultGenKeys<T> runFetchGenKeys(Mapper<T> mapper) {
        return runFetchGenKeys(mapper, PreparedStatementFactory.emptyGenColumns);
    }

    @Override
    public <T> UpdateResultGenKeys<T> runFetchGenKeys(Mapper<T> mapper, String[] genColumns) {
        return runQueryAndFetch(
                ps -> FetchGenKey.yes(mapper).genKeys(ps, ps.executeUpdate()),
                genColumns,
                sqlErrorHandler
        );
    }

    @Override
    public UpdateQuery params(List<?> params) {
        addParameters(params);
        return this;
    }

    @Override
    public UpdateQuery params(Object... params) {
        addParameters(params);
        return this;
    }

    @Override
    public UpdateQuery namedParams(Map<String, ?> namedParams) {
        addNamedParameters(namedParams);
        return this;
    }

    @Override
    public UpdateQuery namedParam(String name, Object parameter) {
        addNamedParameter(name, parameter);
        return this;
    }


    @Override
    void customizeQuery(PreparedStatement preparedStatement, QueryConfig config) {

    }

}
