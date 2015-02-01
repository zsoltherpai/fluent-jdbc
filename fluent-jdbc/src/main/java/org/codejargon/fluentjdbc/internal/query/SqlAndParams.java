package org.codejargon.fluentjdbc.internal.query;

import java.util.List;

public class SqlAndParams {
    private final String sql;
    private final List<Object> params;

    public SqlAndParams(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String sql() {
        return sql;
    }

    public List<Object> params() {
        return params;
    }
}
