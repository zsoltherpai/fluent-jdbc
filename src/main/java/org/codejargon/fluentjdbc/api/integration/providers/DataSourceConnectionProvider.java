package org.codejargon.fluentjdbc.api.integration.providers;

import org.codejargon.fluentjdbc.api.integration.QueryConnectionReceiver;
import org.codejargon.fluentjdbc.api.integration.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConnectionProvider implements ConnectionProvider {
    private final DataSource dataSource;

    public DataSourceConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void provide(QueryConnectionReceiver query) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            query.receive(connection);
        }
    }
}
