package org.codejargon.fluentjdbc.internal.query.namedparameter;

import org.codejargon.fluentjdbc.internal.query.SqlAndParams;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class SqlAndParamsForNamed {
    public static SqlAndParams create(NamedTransformedSql namedTransformedSql, Map<String, Object> namedParams) {
        Preconditions.checkArgument(
                namedTransformedSql.unnamedParameterCount() == 0,
                String.format("Querying with named parameters cannot be run with SQL statements containing positional parameters: %s", namedTransformedSql.sql())
        );
        return new SqlAndParams(
                namedTransformedSql.sql(),
                params(namedTransformedSql.parsedSql(), namedParams)
        );
    }

    public static List<Object> params(ParsedSql parsedSql, Map<String, Object> namedParams) {
        return Arrays.asList(NamedParameterUtils.buildValueArray(parsedSql, namedParams));
    }
}
