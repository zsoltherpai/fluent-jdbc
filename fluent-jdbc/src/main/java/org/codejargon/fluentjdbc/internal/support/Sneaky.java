package org.codejargon.fluentjdbc.internal.support;

import java.util.function.Consumer;

public class Sneaky {
    @FunctionalInterface
    public static interface SneakyConsumer<T>{
        void accept(T elem) throws Exception;
    }

    public static <T> Consumer<T> consumer(SneakyConsumer<T> c) {
        return elem -> {
            try {
                c.accept(elem);
            } catch (Exception ex) {
                Sneaky.<RuntimeException>sneakyException(ex);
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyException(Throwable t) throws T {
        throw (T) t;
    }
}
