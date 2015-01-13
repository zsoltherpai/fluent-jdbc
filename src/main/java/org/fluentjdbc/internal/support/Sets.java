package org.fluentjdbc.internal.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> Set<T> immutableOf() {
        return Collections.unmodifiableSet(new HashSet<>());
    }

    public static <T> Set<T> immutableOf(T[] values) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values)));
    }

    public static <T> Set<T> merge(Set<T> set1, Set<T> set2) {
        Set<T> merged = new HashSet<>(set1);
        merged.addAll(set2);
        return Collections.unmodifiableSet(merged);
    }
}
