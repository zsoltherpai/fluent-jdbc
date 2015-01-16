package org.fluentjdbc.internal.support;

import java.util.*;

public class Lists {
    public static <T> List<T> of(T element1, T element2) {
        return copyOf(Arrays.asList(element1, element2));
    }

    public static <T> List<T> copyOf(Collection<T> collection) {
        return Collections.unmodifiableList(new ArrayList<>(collection));
    }
}
