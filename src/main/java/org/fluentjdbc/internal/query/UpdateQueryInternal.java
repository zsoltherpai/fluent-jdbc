package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateQuery;
import org.fluentjdbc.api.query.UpdateResult;
import java.sql.PreparedStatement;
import java.util.List;

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
            try (PreparedStatement ps = query.preparedStatement(connection, sql, params)) {
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
}
