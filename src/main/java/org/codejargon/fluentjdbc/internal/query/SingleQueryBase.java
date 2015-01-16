package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.util.*;

abstract class SingleQueryBase {
    protected final List<Object> params = new ArrayList<>();
    protected final Map<String, Object> namedParams = new HashMap<>();

    protected void addParameters(List<Object> params) {
        Preconditions.checkArgument(namedParams.isEmpty(), "Can not add positional parameters if named parameters are set.");
        this.params.addAll(params);
    }

    protected void addParameters(Object... params) {
        addParameters(Arrays.asList(params));
    }

    protected void addNamedParameters(Map<String, Object> namedParams) {
        Preconditions.checkArgument(params.isEmpty(), "Can not add named parameters if positional parameters are set.");
        Preconditions.checkArgument(!namedParams.isEmpty(), "Can not set empty named parameters");
        this.namedParams.putAll(namedParams);
    }
}
