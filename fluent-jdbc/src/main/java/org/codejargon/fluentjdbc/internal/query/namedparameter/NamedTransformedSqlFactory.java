package org.codejargon.fluentjdbc.internal.query.namedparameter;

import java.util.*;
import java.util.stream.Collectors;

public class NamedTransformedSqlFactory {
    private final Map<CacheKey, NamedTransformedSql> namedTransformedSqls;

    public NamedTransformedSqlFactory() {
        this.namedTransformedSqls = Collections.synchronizedMap(new WeakHashMap<>());
    }

    public NamedTransformedSql create(String sql, Map<String, ?> namedParams) {
        CacheKey cacheKey = cacheKey(sql, namedParams);
        NamedTransformedSql namedTransformedSql = namedTransformedSqls.get(cacheKey);
        if (namedTransformedSql == null) {
            namedTransformedSql = NamedTransformedSql.forSqlAndParams(sql, namedParams);
            namedTransformedSqls.put(cacheKey, namedTransformedSql);
        }
        return namedTransformedSql;
    }

    public static boolean hasCollection(Map<String, ?> namedParams) {
        return namedParams.values().stream().filter(value -> value instanceof Collection).findFirst().isPresent();
    }

    private CacheKey cacheKey(String sql, Map<String, ?> namedParams) {
        Optional<Map<String, Integer>> paramCounts = hasCollection(namedParams) ?
                Optional.of(
                        Collections.unmodifiableMap(new TreeMap<>(namedParams).entrySet().stream()
                                .collect(
                                        Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> (e.getValue() instanceof Collection) ?
                                                        ((Collection) e.getValue()).size() :
                                                        1
                                        )
                                )
                        )
                ) :
                Optional.empty();
        return new CacheKey(sql, paramCounts);
    }

    private static class CacheKey {
        private final String sql;
        private final Optional<Map<String, Integer>> paramCounts;

        private CacheKey(String sql, Optional<Map<String, Integer>> paramCounts) {
            this.sql = sql;
            this.paramCounts = paramCounts;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey that = (CacheKey) o;

            if (!sql.equals(that.sql)) return false;
            return paramCounts.equals(that.paramCounts);

        }

        @Override
        public int hashCode() {
            int result = sql.hashCode();
            result = 31 * result + paramCounts.hashCode();
            return result;
        }
    }
}
