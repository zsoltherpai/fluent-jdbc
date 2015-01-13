package org.fluentjdbc.api.query;

import java.util.Iterator;
import java.util.List;

public interface BatchQuery {
    /**
     * Sets batch parameters
     */
    BatchQuery params(Iterator<List<Object>> params);

    /**
     * Sets size of the batch
     */
    BatchQuery batchSize(Integer batchSize);

    /**
     * Runs the batch update/insert and returns the results (eg affected rows)
     */
    List<UpdateResult> run();
}
