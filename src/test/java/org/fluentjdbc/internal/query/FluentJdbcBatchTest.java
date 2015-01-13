package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;
import org.fluentjdbc.internal.support.Ints;
import org.junit.Assert;
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

        assertThat(updated.size(), is(2));
        assertThat(updated.get(0).updated(), is(equalTo((long) expectedUpdated[0])));
        assertThat(updated.get(1).updated(), is(equalTo((long) expectedUpdated[1])));

        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(1, param2);
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
        verify(preparedStatement).close();

    }
}
