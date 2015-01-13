package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;
import org.junit.Test;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FluentJdbcUpdateTest extends UpdateTestBase {
    @Test
    public void update() throws SQLException {
        Long expectedUpdatedRows = 5L;
        when(preparedStatement.executeUpdate()).thenReturn(expectedUpdatedRows.intValue());
        UpdateResult updateResult = fluentJdbc.query().update(query).params(param1, param2).run();
        assertThat(updateResult.updated(), is(equalTo(expectedUpdatedRows)));
        verifyQuerying();
        verify(preparedStatement).executeUpdate();
    }

    private void verifyQuerying() throws SQLException {
        verify(connection).prepareStatement(query);
        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(2, param2);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }
}
