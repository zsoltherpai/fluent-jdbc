package org.codejargon.fluentjdbc.internal.support;

import java.util.function.Predicate;

public class Predicates {
    private static Predicate alwaysTrue = x -> true;
    
    public static Predicate alwaysTrue() {
        return alwaysTrue;
    }
}
