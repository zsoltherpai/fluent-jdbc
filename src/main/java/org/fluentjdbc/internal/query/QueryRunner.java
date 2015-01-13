package org.fluentjdbc.internal.query;

import java.sql.Connection;
import java.sql.SQLException;

public interface QueryRunner<T> {
    T run(Connection c) throws SQLException;
}
