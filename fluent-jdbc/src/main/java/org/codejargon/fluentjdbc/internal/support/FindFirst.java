package org.codejargon.fluentjdbc.internal.support;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FindFirst<T> {
    private static final Predicate notNull = e -> e != null;
    private static final Predicate<Optional> isPresent = Optional::isPresent;

    private Optional<List<T>> elements;
    private Optional<Iterable<Supplier<T>>> elementSuppliers;

    public FindFirst(
            Optional<List<T>> elements,
            Optional<Iterable<Supplier<T>>> elementSuppliers
    ) {
        this.elements = elements;
        this.elementSuppliers = elementSuppliers;
    }


    @SafeVarargs
    public static <T> FindFirst<T> from(T... elements) {
        return from(Arrays.asList(elements));
    }

    public static <T> FindFirst<T> from(List<T> elements) {
        return new FindFirst<>(Optional.of(elements), Optional.empty());
    }

    @SafeVarargs
    public static <T> FindFirst<T> fromLazy(Supplier<T>... suppliers) {
        return fromLazy(Arrays.asList(suppliers));
    }

    public static <T> FindFirst<T> fromLazy(Iterable<Supplier<T>> suppliers) {
        return new FindFirst<>(Optional.empty(), Optional.of(suppliers));
    }

    public Optional<T> which(Predicate<T> predicate) {
        return stream().filter(predicate).findFirst();
    }

    @SuppressWarnings("unchecked")
    public Optional<T> whichIsNotNull() {
        return which(notNull);
    }

    @SuppressWarnings("unchecked")
    public Optional<T> whichIsPresent() {
        return which((Predicate) isPresent);
    }

    private Stream<T> stream() {
        return elements.isPresent() ?
                elements.get().stream() :
                stream(elementSuppliers.get()).map(Supplier::get);
    }

    private static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }


}
