package org.codejargon.fluentjdbc.integration.testdata;

import org.codejargon.fluentjdbc.internal.support.Arrs;
import org.codejargon.fluentjdbc.internal.support.Lists;
import org.codejargon.fluentjdbc.internal.support.Maps;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DummyTool {

    public static List<Object> params(Dummy dummy) {
        List<Object> params = new ArrayList<>();
        params.add(dummy.id);
        params.add(dummy.string);
        params.add(dummy.dateLocalDate);
        params.add(dummy.dateSqlDate);
        params.add(dummy.nullString);
        return Lists.copyOf(params);
    }

    public static Map<String, Object> namedParams(Dummy dummy) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", dummy.id);
        params.put("string", dummy.string);
        params.put("dateLocalDate", dummy.dateLocalDate);
        params.put("dateSqlDate", dummy.dateSqlDate);
        params.put("nullString", dummy.nullString);
        return Maps.copyOf(params);
    }

    public static Iterator<Map<String, Object>> namedBatchParams(Dummy... dummies) {
        List<Map<String, Object>> allParams = new ArrayList<>();
        Arrs.stream(dummies).forEach(
                dummy -> allParams.add(namedParams(dummy))
        );
        return Lists.copyOf(allParams).iterator();
    }

    public static Iterator<List<Object>> batchParams(Dummy... dummies) {
        List<List<Object>> allParams = new ArrayList<>();
        Arrs.stream(dummies).forEach(
                dummy -> {
                    allParams.add(params(dummy));
                }
        );
        return allParams.iterator();
    }

    public static void verifyDummy(Dummy actual, Dummy expected) {
        assertThat(actual.id, is(equalTo(expected.id)));
        assertThat(actual.string, is(equalTo(expected.string)));
        assertThat(actual.dateLocalDate, is(equalTo(expected.dateLocalDate)));
        assertThat(actual.dateSqlDate.toLocalDate(), is(equalTo(expected.dateSqlDate.toLocalDate())));
        assertThat(actual.nullString, is(equalTo(expected.nullString)));
    }
}
