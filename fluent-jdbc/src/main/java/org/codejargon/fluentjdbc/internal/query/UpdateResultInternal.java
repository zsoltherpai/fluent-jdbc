package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateResult;

class UpdateResultInternal implements UpdateResult {
    private final long affectedRows;

    UpdateResultInternal(long affectedRows) {
        this.affectedRows = affectedRows;
    }

    @Override
    public long affectedRows() {
        return affectedRows;
    }
}
