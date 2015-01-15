package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.FluentJdbc;
import org.fluentjdbc.api.FluentJdbcBuilder;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class UpdateTestBase {
    static final String query = "UPDATE FOO SET BAR = 'x' WHERE COL1 = ? AND COL2 = ?";
    static final String queryWithNamedParams = "UPDATE FOO SET BAR = 'x' WHERE COL1 = :param1 AND COL2 = :param2";
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

    protected Map<String, Object> namedParams() {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put("param1", param1);
        namedParams.put("param2", param2);
        return namedParams;
    }
}
