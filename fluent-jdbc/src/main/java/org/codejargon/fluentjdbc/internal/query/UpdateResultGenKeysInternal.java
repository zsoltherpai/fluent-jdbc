package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.util.List;

class UpdateResultGenKeysInternal<T> extends UpdateResultInternal implements UpdateResultGenKeys<T> {
    private final List<T> generatedKeys;

    UpdateResultGenKeysInternal(Long affectedRows, List<T> generatedKeys) {
        super(affectedRows);
        this.generatedKeys = generatedKeys;
    }

    @Override
    public List<T> generatedKeys() {
        return generatedKeys;
    }
}
