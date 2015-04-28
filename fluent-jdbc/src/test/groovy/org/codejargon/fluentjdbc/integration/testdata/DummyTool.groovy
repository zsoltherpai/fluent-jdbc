package org.codejargon.fluentjdbc.integration.testdata

public class DummyTool {

    public static Iterator<Map<String, Object>> namedBatchParams(Dummy... dummies) {
        List<Map<String, Object>> allParams = []
        dummies.each {
            dummy -> allParams.add(dummy.namedParams())
        }
        return allParams.iterator()
    }

    public static Iterator<List<Object>> batchParams(Dummy... dummies) {
        List<List<Object>> allParams = []
        dummies.each {
            dummy ->
                allParams.add(dummy.params())

        }
        return allParams.iterator()
    }

    static void assertDummy(Dummy actual, Dummy expected) {
        assert actual.id.equals(expected.id)
        assert actual.string.equals(expected.string)
        assert actual.dateLocalDate.equals(expected.dateLocalDate)
        assert actual.dateSqlDate.toLocalDate().equals(expected.dateSqlDate.toLocalDate())
        assert actual.nullString == null && expected.nullString == null
    }
}
