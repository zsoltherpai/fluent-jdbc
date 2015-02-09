package org.codejargon.fluentjdbc.internal.support;

import java.util.*;

public class Lists {
    private static final List<?> emptyList = Collections.unmodifiableList(new ArrayList<>());
    
    
    public static <T> List<T> copyOf(T[] elements) {
        return copyOf(Arrays.asList(elements));
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> copyOf(Collection<T> collection) {
        return collection.size() > 0 ? Collections.unmodifiableList(new ArrayList<>(collection)) : (List<T>) emptyList;
    }
}
