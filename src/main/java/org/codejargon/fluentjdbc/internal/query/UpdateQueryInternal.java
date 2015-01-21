package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class UpdateQueryInternal extends SingleQueryBase implements UpdateQuery {
    private final String sql;
    private final QueryInternal query;

    public UpdateQueryInternal(String sql, QueryInternal query) {
        super();
        this.sql = sql;
        this.query = query;
    }

    @Override
    public UpdateResult run() {
        return query.query(connection -> {
            try (PreparedStatement ps = query.preparedStatement(connection, querySpecs())) {
                return new UpdateResultInternal((long) ps.executeUpdate());
            }
        }, sql);
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
    
    private SingleQuerySpecification querySpecs() {
        return SingleQuerySpecification.forUpdate(sql, params, namedParams);
    }
}
