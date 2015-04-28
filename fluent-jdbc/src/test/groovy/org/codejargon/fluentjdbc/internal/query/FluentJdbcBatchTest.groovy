package org.codejargon.fluentjdbc.internal.query
import org.codejargon.fluentjdbc.api.query.UpdateResult

class FluentJdbcBatchTest extends UpdateTestBase {
    static def param3 = "param3"
    static def param4 = "param4"
    static def param5 = "param5"
    static def param6 = "param6"

    def "Batch update"() {
        given:
        int[] expectedUpdated = [1, 1]
        Iterator<List<Object>> params = [
                [param1, param2],
                [param3, param4]
        ].iterator()
        preparedStatement.executeBatch() >> expectedUpdated
        when:
        def updated = query.batch(sql).params(params).run()
        then:
        assertUpdateResults(expectedUpdated, updated)
        interaction {
            verify4Params()
            verifyBatches(2)
        }
    }

    def "Batch update with batch size specified"() {
        given:
        int[] expectedUpdated = [1, 1, 1]
        Iterator<List<Object>> params = Arrays.<List<Object>>asList(
                Arrays.asList(param1, param2),
                Arrays.asList(param3, param4),
                Arrays.asList(param5, param6)
        ).iterator()
        preparedStatement.executeBatch() >> [1, 1] >> [1]
        when:
        def updated = query.batch(sql).batchSize(2).params(params).run()
        then:
        assertUpdateResults(expectedUpdated, updated)
        interaction {
            verify6Params()
            verifyBatches(3)
        }
    }

    def "Batch update with named parameters"() {
        given:
        int[] expectedUpdated = [1];
        Iterator<Map<String, Object>> namedParams = Arrays.<Map<String, Object>>asList(
                namedParams()
        ).iterator();
        connection.prepareStatement(sql) >> preparedStatement
        preparedStatement.executeBatch() >> expectedUpdated
        when:
        query.batch(sqlWithNamedParams).namedParams(namedParams).run();
        then:
        interaction {
            verify2Params();
            verifyBatches(1);
        }
    }


    def verifyBatches(Integer addBatch) {
        addBatch * preparedStatement.addBatch()
        1 * preparedStatement.close()
    }

    def assertUpdateResults(int[] expectedUpdated, List<UpdateResult> updated) {
        assert updated.size() == expectedUpdated.length
        updated.eachWithIndex { UpdateResult result, int i ->
            result.affectedRows() == (long) expectedUpdated[i]
        }
    }

    def verify2Params() {
        1 * preparedStatement.setObject(1, param1)
        1 * preparedStatement.setObject(2, param2)
    }

    def verify4Params() {
        verify2Params();
        1 * preparedStatement.setObject(1, param3)
        1 * preparedStatement.setObject(2, param4)
    }

    def verify6Params() {
        verify4Params();
        1 * preparedStatement.setObject(1, param5)
        1 * preparedStatement.setObject(2, param6)
    }

}
