package org.fluentjdbc.internal.query.namedparameter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NamedSqlAndParams {
    private final String sql;
    private final List<Object> params;

    public NamedSqlAndParams(String transformedSql, ParsedSql parsedSql, Map<String, Object> namedParams) {
        sql = transformedSql;
        this.params = params(parsedSql, namedParams);
    }

    public String sql() {
        return sql;
    }

    public List<Object> params() {
        return params;
    }

    private List<Object> params(ParsedSql parsedSql, Map<String, Object> namedParams) {
        return Arrays.asList(NamedParameterUtils.buildValueArray(parsedSql, namedParams));
    }
}
