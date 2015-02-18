package org.codejargon.fluentjdbc.internal.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryRunnerPreparedStatement<T> {
    T run(PreparedStatement c) throws SQLException;
}
