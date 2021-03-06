package org.codejargon.fluentjdbc.internal.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Maps {
    public static <K, V> Map<K, V> copyOf(Map<K, V> map) {
        return Collections.unmodifiableMap(new HashMap<>(map));
    }

    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> iterable, Function<V, K> keyFunction) {
        return Collections.unmodifiableMap(
                Iterables.stream(iterable).collect(
                        Collectors.toMap(
                                keyFunction,
                                value -> value
                        )
                )
        );
    }
    
    public static <K, V> Map<K, V> merge(Map<K, V> map1, Map<K, V> map2) {
        Map<K, V> merged = new HashMap<>(map1);
        merged.putAll(map2);
        return Collections.unmodifiableMap(merged);
    }
}
