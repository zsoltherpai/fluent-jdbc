package org.fluentjdbc.internal.query;

import java.util.List;

public interface SqlAndParams {
    String sql();
    List<Object> params();
}
