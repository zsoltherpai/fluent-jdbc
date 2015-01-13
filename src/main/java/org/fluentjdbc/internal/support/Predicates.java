package org.fluentjdbc.internal.support;

import java.util.function.Predicate;

public class Predicates {
    public static Predicate alwaysTrue() {
        return x -> true;
    }
}
