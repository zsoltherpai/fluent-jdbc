package org.codejargon.fluentjdbc.internal.query.namedparameter;

import org.codejargon.fluentjdbc.internal.query.SqlAndParams;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.util.*;

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

    public static List<Object> params(ParsedSql parsedSql, Map<String, ?> namedParams) {
        return flatten(NamedParameterUtils.buildValueArray(parsedSql, namedParams));
    }

    @SuppressWarnings("unchecked")
    private static List<Object> flatten(Object[] params) {
        List flattened = new ArrayList<>();
        for (Object param : params) {
            if (param instanceof Collection) {
                flattened.addAll((Collection) param);
            } else {
                flattened.add(param);
            }
        }
        return Collections.unmodifiableList(flattened);
    }
}
