package org.fluentjdbc.internal.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Lists {
    public static <T> List<T> immutableCopyOf(Collection<T> collection) {
        return Collections.unmodifiableList(new ArrayList<>(collection));
    }
}
