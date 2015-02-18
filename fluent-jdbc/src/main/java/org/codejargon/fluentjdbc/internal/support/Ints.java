package org.codejargon.fluentjdbc.internal.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ints {
    public static List<Integer> asList(int[] ints) {
        List<Integer> integers = new ArrayList<>(ints.length);
        for(int i : ints) {
            integers.add(i);
        }
        return Collections.unmodifiableList(integers);
    }
}
