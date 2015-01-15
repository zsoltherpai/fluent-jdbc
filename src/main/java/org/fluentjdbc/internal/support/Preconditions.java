package org.fluentjdbc.internal.support;

import org.fluentjdbc.api.FluentJdbcException;

public class Preconditions {
    public static void checkNotNull(Object obj, String description) {
        if(obj == null) {
            throw new FluentJdbcException(description + " cannot be null.");
        }
    }
    
    public static void checkArgument(Boolean arg, String description) {
        if(!arg) {
            throw new FluentJdbcException(description);
        }
    }
}
