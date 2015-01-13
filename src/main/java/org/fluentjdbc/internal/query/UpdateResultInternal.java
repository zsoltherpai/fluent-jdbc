package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.UpdateResult;

public class UpdateResultInternal implements UpdateResult {
    private final Long updated;

    public UpdateResultInternal(Long updated) {
        this.updated = updated;
    }

    @Override
    public Long updated() {
        return updated;
    }
}
