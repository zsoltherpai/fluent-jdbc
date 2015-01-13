package org.fluentjdbc.api.query;

import java.util.List;

public interface UpdateQuery {
    /**
     * Adds query parameters
     */
    UpdateQuery params(List<Object> params);
    /**
     * Adds query parameters
     */
    UpdateQuery params(Object... params);

    /**
     * Runs the update query and returns the result of it (eg affected rows)
     */
    UpdateResult run();
}
