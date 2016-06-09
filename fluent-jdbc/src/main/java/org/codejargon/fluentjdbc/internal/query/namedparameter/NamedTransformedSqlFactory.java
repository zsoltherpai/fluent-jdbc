package org.codejargon.fluentjdbc.internal.query.namedparameter;

import java.util.*;
import java.util.stream.Collectors;

public class NamedTransformedSqlFactory {
    private final Map<String, NamedTransformedSql> namedParamSqlCacheNoCollections;
    private final Map<Map<String, Integer>, NamedTransformedSql> namedParamCacheWithCollections;

    public NamedTransformedSqlFactory() {
        this.namedParamSqlCacheNoCollections = Collections.synchronizedMap(new WeakHashMap<>());
        this.namedParamCacheWithCollections = Collections.synchronizedMap(new WeakHashMap<>());
    }

    public NamedTransformedSql create(String sql, Map<String, ?> namedParams) {
        if (hasCollection(namedParams)) {
            Map<String, Integer> cacheKey = collectionParamCacheKey(namedParams);
            NamedTransformedSql namedTransformedSql = namedParamCacheWithCollections.get(cacheKey);
            if(namedTransformedSql == null) {
                namedTransformedSql = NamedTransformedSql.forSqlAndParams(sql, namedParams);
                namedParamCacheWithCollections.put(cacheKey, namedTransformedSql);
            }
            return namedTransformedSql;
        } else {
            NamedTransformedSql namedTransformedSql = namedParamSqlCacheNoCollections.get(sql);
            if (namedTransformedSql == null) {
                namedTransformedSql = NamedTransformedSql.forSqlAndParams(sql, namedParams);
                namedParamSqlCacheNoCollections.put(sql, namedTransformedSql);
            }
            return namedTransformedSql;
        }
    }

    public static boolean hasCollection(Map<String, ?> namedParams) {
        return namedParams.values().stream().filter(value -> value instanceof Collection).findFirst().isPresent();
    }

    private Map<String, Integer> collectionParamCacheKey(Map<String, ?> namedParams) {
        return Collections.unmodifiableMap(new TreeMap<>(namedParams).entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof Collection)
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> ((Collection) e.getValue()).size()))
        );
    }
}
