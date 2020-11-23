package org.codejargon.fluentjdbc.internal.query;

import java.sql.SQLException;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.query.listen.ExecutionDetails;

class ExecutionDetailsInternal implements ExecutionDetails {
    private final QueryInfoInternal queryInfo;
    private final Long executionTimeMs;
    private final Optional<SQLException> sqlException;

    public ExecutionDetailsInternal(
            QueryInfoInternal queryInfo,
            Long executionTimeMs,
            Optional<SQLException> sqlException) {
        this.queryInfo = queryInfo;
        this.executionTimeMs = executionTimeMs;
        this.sqlException = sqlException;
    }

    @Override
    public Boolean success() {
        return !sqlException.isPresent();
    }

    @Override
    public QueryInfoInternal queryInfo() {
        return queryInfo;
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
