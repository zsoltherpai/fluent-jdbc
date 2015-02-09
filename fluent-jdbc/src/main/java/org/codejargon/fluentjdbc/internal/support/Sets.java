package org.codejargon.fluentjdbc.internal.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    private static final Set<?> emptySet = Collections.unmodifiableSet(new HashSet<>());
    
    @SuppressWarnings("unchecked")
    public static <T> Set<T> empty() {
        return (Set<T>) emptySet;
    }

    public static <T> Set<T> immutableOf(T[] values) {
        return values.length > 0 ? Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values))) : empty();
    }

    public static <T> Set<T> merge(Set<T> set1, Set<T> set2) {
        Set<T> merged = new HashSet<>(set1);
        merged.addAll(set2);
        return Collections.unmodifiableSet(merged);
    }

    public static void main(String[] args) {
        Set<String> strs = empty();
        System.out.println(strs.size());
    }
}
