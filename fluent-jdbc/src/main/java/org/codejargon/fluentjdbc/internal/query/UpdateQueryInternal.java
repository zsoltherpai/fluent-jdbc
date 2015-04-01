package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.api.query.UpdateResultGeneratedKeys;
import org.codejargon.fluentjdbc.internal.support.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class UpdateQueryInternal extends SingleQueryBase implements UpdateQuery {

    public UpdateQueryInternal(String sql, QueryInternal query) {
        super(query, sql);
    }

    @Override
    public UpdateResult run() {
        return runQuery(
                SingleQuerySpecification.forUpdate(this, false),
                ps -> new UpdateResultInternal((long) ps.executeUpdate())
        );
    }

    @Override
    public <T> UpdateResultGeneratedKeys<T> runAndFetchGenerated(Mapper<T> mapper) {
        return runQuery(SingleQuerySpecification.forUpdate(this, true),
                ps -> new UpdateResultGeneratedKeysInternal<>(
                        (long) ps.executeUpdate(),
                        generatedKeys(ps, mapper))
        );
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

    private <T> List<T> generatedKeys(PreparedStatement ps, Mapper<T> generatedKeyMapper) throws SQLException {
        try (ResultSet keySet = ps.getGeneratedKeys()) {
            List<T> keys = new ArrayList<T>(1);
            while (keySet.next()) {
                keys.add(generatedKeyMapper.map(keySet));
            }
            return Collections.unmodifiableList(keys);
        }
    }
}
