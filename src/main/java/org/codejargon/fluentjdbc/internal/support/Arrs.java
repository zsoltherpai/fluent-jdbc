package org.codejargon.fluentjdbc.internal.support;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Arrs {
    public static <T> Stream<T> stream(T[] array) {
        return StreamSupport.stream(Arrays.spliterator(array), false);
    }
}
