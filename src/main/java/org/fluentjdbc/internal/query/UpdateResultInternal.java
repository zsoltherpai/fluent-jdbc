package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;

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
