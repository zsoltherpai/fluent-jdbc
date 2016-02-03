package org.codejargon.fluentjdbc.api.query.listen;

import java.sql.SQLException;
import java.util.Optional;

public interface ExecutionDetails {
    Boolean success();
    String sql();
    Long executionTimeMs();
    Optional<SQLException> sqlException();
}
