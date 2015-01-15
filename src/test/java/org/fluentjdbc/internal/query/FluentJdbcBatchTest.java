package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FluentJdbcBatchTest extends UpdateTestBase {
    static String param3 = "param3";
    static String param4 = "param4";
    static String param5 = "param5";
    static String param6 = "param6";

    @Test
    public void batch() throws SQLException {
        int[] expectedUpdated = new int[]{1, 1};
        Iterator<List<Object>> params = Arrays.<List<Object>>asList(
            Arrays.asList(param1, param2),
            Arrays.asList(param3, param4)
        ).iterator();
        when(preparedStatement.executeBatch()).thenReturn(expectedUpdated);

        List<UpdateResult> updated = fluentJdbc.query().batch(query).params(params).run();
        verifyUpdateResults(expectedUpdated, updated);
        verify4Params();
        verifyQuery(2, 1);
    }

    @Test
    public void batchUpdateWithBatchSize() throws SQLException {
        int[] expectedUpdated = new int[]{1, 1, 1};
        Iterator<List<Object>> params = Arrays.<List<Object>>asList(
                Arrays.asList(param1, param2),
                Arrays.asList(param3, param4),
                Arrays.asList(param5, param6)
        ).iterator();
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1}, new int[]{1});
        List<UpdateResult> updated = fluentJdbc.query().batch(query).batchSize(2).params(params).run();
        verifyUpdateResults(expectedUpdated, updated);
        verify6Params();
        verifyQuery(3, 2);

    }

    @Test
    public void batchWithNamedParams() throws SQLException {
        int[] expectedUpdated = new int[]{1};
        Iterator<Map<String, Object>> namedParams = Arrays.<Map<String, Object>>asList(
                namedParams()
        ).iterator();
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(expectedUpdated);
        fluentJdbc.query().batch(queryWithNamedParams).namedParams(namedParams).run();

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());
        assertThat(queryCaptor.getValue(), is(equalTo(query)));

        verify2Params();
        verifyQuery(1, 1);
    }

    private void verifyQuery(Integer addBatch, Integer executeBatch) throws SQLException {
        verify(preparedStatement, times(addBatch)).addBatch();
        verify(preparedStatement, times(executeBatch)).executeBatch();
        verify(preparedStatement).close();
    }

    private void verifyUpdateResults(int[] expectedUpdated, List<UpdateResult> updated) {
        assertThat(updated.size(), is(expectedUpdated.length));
        for(int i = 0; i < updated.size(); ++i) {
            assertThat(updated.get(i).affectedRows(), is(equalTo((long) expectedUpdated[i])));
        }
    }

    private void verify2Params() throws SQLException {
        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(2, param2);
    }

    private void verify4Params() throws SQLException {
        verify2Params();
        verify(preparedStatement).setObject(1, param3);
        verify(preparedStatement).setObject(2, param4);
    }

    private void verify6Params() throws SQLException {
        verify4Params();
        verify(preparedStatement).setObject(1, param5);
        verify(preparedStatement).setObject(2, param6);
    }

}
