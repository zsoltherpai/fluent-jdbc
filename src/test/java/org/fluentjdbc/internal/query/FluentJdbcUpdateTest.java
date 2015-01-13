package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcBuilder;
import org.fluentjdbc.api.query.UpdateResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FluentJdbcUpdateTest {

    static final String query = "UPDATE FOO SET BAR = 'x' WHERE COL1 = ? AND COL2 = ?";
    static final String param1 = "lille";
    static final String param2 = "lamb";

    @Mock
    Connection connection;
    @Mock
    PreparedStatement preparedStatement;
    FluentJdbc fluentJdbc;

    @Before
    public void setUp() throws SQLException {
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        fluentJdbc = new FluentJdbcBuilder().connectionProvider((q) -> {
            q.receive(connection);
        }).build();
    }

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
        verify(preparedStatement).close();

    }
}
