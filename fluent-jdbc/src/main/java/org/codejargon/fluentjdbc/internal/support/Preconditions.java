package org.codejargon.fluentjdbc.internal.support;

import org.codejargon.fluentjdbc.api.FluentJdbcException;

import java.util.Optional;

public class Preconditions {
    public static void checkNotNull(Object obj, String description) {
        if(obj == null) {
            throw new FluentJdbcException(description + " cannot be null.");
        }
    }

    public static <T> void checkPresent(Optional<T> obj, String description) {
        if(!obj.isPresent()) {
            throw new FluentJdbcException(description + " must be present.");
        }
    }
    
    public static void checkArgument(Boolean arg, String description) {
        if(!arg) {
            throw new FluentJdbcException(description);
        }
    }
}
