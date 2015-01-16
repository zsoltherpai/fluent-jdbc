package org.codejargon.fluentjdbc.internal.query.namedparameter;

public class TransformedSql {
    private final String transformedSql;
    private final ParsedSql parsedSql;

    public static TransformedSql forSql(String sql) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        return new TransformedSql(NamedParameterUtils.substituteNamedParameters(parsedSql), parsedSql);
    }

    TransformedSql(String transformedSql, ParsedSql parsedSql) {
        this.transformedSql = transformedSql;
        this.parsedSql = parsedSql;
    }

    public String sql() {
        return transformedSql;
    }

    public ParsedSql parsedSql() {
        return parsedSql;
    }

    public Integer unnamedParameterCount() {
        return parsedSql.getUnnamedParameterCount();
    }
}
