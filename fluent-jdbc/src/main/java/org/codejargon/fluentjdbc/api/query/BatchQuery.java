package org.codejargon.fluentjdbc.api.query;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Batch insert or update query for a SQL statement. Is a mutable object.
 */
public interface BatchQuery {
    /**
     *
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery param(List<?> params);
    
    /**
    *
    * @param params Parameters used by the batch update
    * @return this
    */
    BatchQuery params(Iterator<List<?>> params);
    /**
     *
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery params(Iterable<List<?>> params);

    /**
     *
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery params(Stream<List<?>> params);

    /**
     * 
     * @param name Parameter name
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery namedParam(String name, List<?> params);
    
    /**
    *
    * @param params Parameters used by the batch update
    * @return this
    */
    BatchQuery namedParams(Iterator<Map<String, ?>> params);
    
    /**
     *
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery namedParams(Iterable<Map<String, ?>> params);

    /**
     *
     * @param params Parameters used by the batch update
     * @return this
     */
    BatchQuery namedParams(Stream<Map<String, ?>> params);

    /**
     * Sets size of a batch (database roundtrip)
     * @param batchSize size of a batch
     * @return this
     */
    BatchQuery batchSize(Integer batchSize);

    /**
     * Runs the batch insert or update and returns the results (eg affected rows)
     *
     * @return List of update results
     */
    List<UpdateResult> run();
}
