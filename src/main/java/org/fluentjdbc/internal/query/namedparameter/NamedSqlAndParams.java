package org.fluentjdbc.internal.query.namedparameter;

import org.fluentjdbc.internal.query.SqlAndParams;
import org.fluentjdbc.internal.support.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class NamedSqlAndParams {
    public static SqlAndParams sqlAndParams(TransformedSql transformedSql, Map<String, Object> namedParams) {
        Preconditions.checkArgument(
                transformedSql.unnamedParameterCount() == 0,
                String.format("Querying with named parameters cannot be run with SQL statements containing positional parameters: %s", transformedSql.sql())
        );
        return new SqlAndParams(
                transformedSql.sql(),
                params(transformedSql.parsedSql(), namedParams)
        );
    }

    private static List<Object> params(ParsedSql parsedSql, Map<String, Object> namedParams) {
        return Arrays.asList(NamedParameterUtils.buildValueArray(parsedSql, namedParams));
    }
}
