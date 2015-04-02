package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FluentJdbcUpdateTest extends UpdateTestBase {
    static Long expectedUpdatedRows = 5L;

    @Test
    public void update() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(expectedUpdatedRows.intValue());
        UpdateResult updateResult = query.update(sql).params(param1, param2).run();
        assertThat(updateResult.affectedRows(), is(equalTo(expectedUpdatedRows)));
        verifyQuerying(false);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    public void updateWithNamedParams() throws SQLException {
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(expectedUpdatedRows.intValue());
        query.update(sqlWithNamedParams).namedParams(namedParams()).run();
        verifyQuerying(false);
    }

    @Test
    public void updateAndFetchGeneratedKeys() throws SQLException {
        Long generatedKey = 5L;
        ResultSet genKeyRs = mock(ResultSet.class);
        when(genKeyRs.next()).thenReturn(true).thenReturn(false);
        when(genKeyRs.getLong(1)).thenReturn(generatedKey);
        when(preparedStatement.getGeneratedKeys()).thenReturn(genKeyRs);
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);

        UpdateResultGenKeys result = query.update(sql).params(param1, param2).runFetchGenKeys(Mappers.singleLong());
        assertThat(result.generatedKeys().size(), is(1));
        assertThat(result.generatedKeys().get(0), is(equalTo(generatedKey)));

        /// verify query
        verifyQuerying(true);
        verify(preparedStatement).getGeneratedKeys();
        verify(genKeyRs, times(2)).next();
        verify(genKeyRs).getLong(1);

    }

    private void verifyQuerying(Boolean withGenerated) throws SQLException {
        if(withGenerated) {
            verify(connection).prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            verify(connection).prepareStatement(sql);
        }

        verify(preparedStatement).setObject(1, param1);
        verify(preparedStatement).setObject(2, param2);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }
}
