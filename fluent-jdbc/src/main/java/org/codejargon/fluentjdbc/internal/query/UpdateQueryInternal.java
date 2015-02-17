package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

class UpdateQueryInternal extends SingleQueryBase implements UpdateQuery {

    public UpdateQueryInternal(String sql, QueryInternal query) {
        super(query, sql);
    }

    @Override
    public UpdateResult run() {
        return runQuery(ps -> new UpdateResultInternal((long) ps.executeUpdate()));
    }

    @Override
    public UpdateQuery params(List<Object> params) {
        addParameters(params);
        return this;
    }

    @Override
    public UpdateQuery params(Object... params) {
        addParameters(params);
        return this;
    }

    @Override
    public UpdateQuery namedParams(Map<String, Object> namedParams) {
        addNamedParameters(namedParams);
        return this;
    }
    
    @Override
    protected SingleQuerySpecification querySpecs() {
        return SingleQuerySpecification.forUpdate(sql, params, namedParams);
    }
}
