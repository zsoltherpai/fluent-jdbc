package org.fluentjdbc.internal.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import static org.fluentjdbc.internal.support.Iterables.stream;

public class Maps {
    public static <K, V> Map<K, V> immutableCopyOf(Map<K, V> map) {
        return Collections.unmodifiableMap(new HashMap<>(map));
    }

    public static <K, V> Map<K, V> uniqueIndex(Iterable<V> iterable, Function<V, K> keyFunction) {
        return Collections.unmodifiableMap(
                stream(iterable).collect(
                        Collectors.toMap(
                                keyFunction,
                                value -> {
                                    return value;
                                }
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
