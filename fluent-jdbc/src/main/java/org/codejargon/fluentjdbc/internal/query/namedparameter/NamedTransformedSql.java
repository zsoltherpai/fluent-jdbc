package org.codejargon.fluentjdbc.internal.query.namedparameter;

public class NamedTransformedSql {
    private final String transformedSql;
    private final ParsedSql parsedSql;

    public static NamedTransformedSql forSql(String sql) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return new NamedTransformedSql(NamedParameterUtils.substituteNamedParameters(parsedSql), parsedSql);
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
