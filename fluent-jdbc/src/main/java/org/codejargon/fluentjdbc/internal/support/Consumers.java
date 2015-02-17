package org.codejargon.fluentjdbc.internal.support;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.Consumer;

public class Consumers {
    @FunctionalInterface
    public static interface ConsumerCheckException<T>{
        void accept(T elem) throws Exception;
    }

    public static <T> Consumer<T> sneaky(ConsumerCheckException<T> c) {
        return elem -> {
            try {
                c.accept(elem);
            } catch (Exception ex) {
                Consumers.<RuntimeException>sneakyException(ex);
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T sneakyException(Throwable t) throws T {
        throw (T) t;
    }
}
