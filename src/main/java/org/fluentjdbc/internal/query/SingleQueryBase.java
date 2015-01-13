package org.fluentjdbc.internal.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class SingleQueryBase {
    protected final List<Object> params = new ArrayList<>();
    
    public void addParameters(List<Object> params) {
        this.params.addAll(params);
    }
    
    public void addParameters(Object... params) {
        addParameters(Arrays.asList(params));
    }
}
