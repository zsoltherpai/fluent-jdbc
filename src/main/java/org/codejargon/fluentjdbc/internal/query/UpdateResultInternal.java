package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateResult;

public class UpdateResultInternal implements UpdateResult {
    private final Long affectedRows;

    public UpdateResultInternal(Long affectedRows) {
        this.affectedRows = affectedRows;
    }

    @Override
    public Long affectedRows() {
        return affectedRows;
    }
}
