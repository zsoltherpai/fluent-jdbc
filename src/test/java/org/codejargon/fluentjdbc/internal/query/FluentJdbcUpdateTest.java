package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FluentJdbcUpdateTest extends UpdateTestBase {
    static Long expectedUpdatedRows = 5L;

    @Test
    public void update() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(expectedUpdatedRows.intValue());
        UpdateResult updateResult = fluentJdbc.query().update(query).params(param1, param2).run();
        assertThat(updateResult.affectedRows(), is(equalTo(expectedUpdatedRows)));
        verifyQuerying();
        verify(preparedStatement).executeUpdate();
    }

    @Test
    public void updateWithNamedParams() throws SQLException {
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(expectedUpdatedRows.intValue());
        fluentJdbc.query().update(queryWithNamedParams).namedParams(namedParams()).run();
        verifyQuerying();
    }

    private void verifyQuerying() throws SQLException {
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(queryCaptor.capture());
        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(2, param2);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
        assertThat(queryCaptor.getValue(), is(equalTo(query)));
    }
}
