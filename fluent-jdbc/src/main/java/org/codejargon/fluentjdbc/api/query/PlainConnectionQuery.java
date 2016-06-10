package org.codejargon.fluentjdbc.api.query;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface PlainConnectionQuery<T> {
    <T> T operation(Connection con) throws SQLException;
}
