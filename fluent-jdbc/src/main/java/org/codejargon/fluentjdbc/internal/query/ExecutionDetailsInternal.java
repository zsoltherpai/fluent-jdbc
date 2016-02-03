package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.listen.ExecutionDetails;

import java.sql.SQLException;
import java.util.Optional;

class ExecutionDetailsInternal implements ExecutionDetails {
    private final String sql;
    private final Long executionTimeMs;
    private final Optional<SQLException> sqlException;

    public ExecutionDetailsInternal(
            String sql,
            Long executionTimeMs,
            Optional<SQLException> sqlException) {
        this.sql = sql;
        this.executionTimeMs = executionTimeMs;
        this.sqlException = sqlException;
    }

    @Override
    public Boolean success() {
        return !sqlException.isPresent();
    }

    @Override
    public String sql() {
        return sql;
    }

    @Override
    public Long executionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public Optional<SQLException> sqlException() {
        return sqlException;
    }
}
