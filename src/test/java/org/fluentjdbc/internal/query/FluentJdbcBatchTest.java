package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FluentJdbcBatchTest extends UpdateTestBase {
    @Test
    public void batch() throws SQLException {
        int[] expectedUpdated = new int[]{1, 1};
        Iterator<List<Object>> params = Arrays.<List<Object>>asList(
            Arrays.asList(param1),
            Arrays.asList(param2)
        ).iterator();
        when(preparedStatement.executeBatch()).thenReturn(expectedUpdated);

        List<UpdateResult> updated = fluentJdbc.query().batch(query).params(params).run();

        verifyUpdateResults(expectedUpdated, updated);

        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(1, param2);
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
        verify(preparedStatement).close();
    }

    @Test
    public void batchUpdateWithBatchSize() throws SQLException {
        String thirdParam = "3";
        int[] expectedUpdated = new int[]{1, 1, 1};
        Iterator<List<Object>> params = Arrays.<List<Object>>asList(
                Arrays.asList(param1),
                Arrays.asList(param2),
                Arrays.asList(thirdParam)
        ).iterator();
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1}, new int[]{1});
        List<UpdateResult> updated = fluentJdbc.query().batch(query).batchSize(2).params(params).run();

        verifyUpdateResults(expectedUpdated, updated);

        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(1, param2);
        verify(preparedStatement).setObject(1, thirdParam);
        verify(preparedStatement, times(3)).addBatch();
        verify(preparedStatement, times(2)).executeBatch();
        verify(preparedStatement).close();
    }

    private void verifyUpdateResults(int[] expectedUpdated, List<UpdateResult> updated) {
        assertThat(updated.size(), is(expectedUpdated.length));
        for(int i = 0; i < updated.size(); ++i) {
            assertThat(updated.get(i).updated(), is(equalTo((long) expectedUpdated[i])));
        }
    }
}
