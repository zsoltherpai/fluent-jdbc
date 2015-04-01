package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.UpdateResultGeneratedKeys;

import java.util.List;

public class UpdateResultGeneratedKeysInternal<T> extends UpdateResultInternal implements UpdateResultGeneratedKeys<T> {
    private final List<T> generatedKeys;

    public UpdateResultGeneratedKeysInternal(Long affectedRows, List<T> generatedKeys) {
        super(affectedRows);
        this.generatedKeys = generatedKeys;
    }


    @Override
    public List<T> generatedKeys() {
        return generatedKeys;
    }
}
