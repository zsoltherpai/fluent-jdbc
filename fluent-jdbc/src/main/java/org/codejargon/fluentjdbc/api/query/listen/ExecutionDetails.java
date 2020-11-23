package org.codejargon.fluentjdbc.api.query.listen;

import java.sql.SQLException;
import java.util.Optional;

public interface ExecutionDetails {
    Boolean success();
    QueryInfo queryInfo();
    Long executionTimeMs();
    Optional<SQLException> sqlException();
}
