package org.codejargon.fluentjdbc.internal.query.namedparameter;

import java.util.Map;

public class NamedTransformedSql {
    private final String transformedSql;
    private final ParsedSql parsedSql;

    public static NamedTransformedSql forSqlAndParams(String sql, Map<String, ?> namedParams) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return new NamedTransformedSql(NamedParameterUtils.substituteNamedParameters(parsedSql, namedParams), parsedSql);
    }

    NamedTransformedSql(String transformedSql, ParsedSql parsedSql) {
        this.transformedSql = transformedSql;
        this.parsedSql = parsedSql;
    }

    public String sql() {
        return transformedSql;
    }

    public ParsedSql parsedSql() {
        return parsedSql;
    }

    Integer unnamedParameterCount() {
        return parsedSql.getUnnamedParameterCount();
    }
}
