package org.codejargon.fluentjdbc.internal.support;

import java.util.function.Supplier;

public class Select {
    @SafeVarargs
    public static <T> T firstNonNull(Supplier<T>... elements) {
        return Arrs.stream(elements).filter(e -> e.get() != null).findFirst().get().get();
    }
}
