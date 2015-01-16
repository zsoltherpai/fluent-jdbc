package org.fluentjdbc.internal.support;

import java.util.*;

public class Lists {
    public static <T> List<T> copyOf(Collection<T> collection) {
        return Collections.unmodifiableList(new ArrayList<>(collection));
    }
}
