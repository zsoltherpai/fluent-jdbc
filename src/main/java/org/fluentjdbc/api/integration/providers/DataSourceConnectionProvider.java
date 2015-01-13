package org.fluentjdbc.api.integration.providers;

import org.fluentjdbc.api.integration.ConnectionProvider;
import org.fluentjdbc.api.integration.QueryConnectionReceiver;

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
