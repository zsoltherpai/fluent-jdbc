package org.codejargon.fluentjdbc.internal.support;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Iterables {
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    private static <T> Iterable<T> fromIterator(Iterator<T> iterator) {
        return () -> iterator;
    }

    public static <T> Stream<T> streamOfIterator(Iterator<T> iterator) {
        return stream(fromIterator(iterator));
    }
}
