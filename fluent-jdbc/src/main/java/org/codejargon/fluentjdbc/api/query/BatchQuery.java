package org.codejargon.fluentjdbc.api.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Batch insert or update query for a SQL statement. Is a mutable object.
 */
public interface BatchQuery {
    /**
     *
     * @type <C> any instance of {@link Collection}.
     * @param params Parameters used by the batch update
     * @return this
     */
	<C extends Collection<?>> BatchQuery params(Iterator<C> params);

    /**
     *
     * @type <C> any instance of {@link Collection}.
     * @param params Parameters used by the batch update
     * @return this
     */
	<C extends Collection<?>> BatchQuery params(Iterable<C> params);

    /**
     *
     * @type <C> any instance of {@link Collection}.
     * @param params Parameters used by the batch update
     * @return this
     */
	<C extends Collection<?>> BatchQuery params(Stream<C> params);

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
    Collection<UpdateResult> run();
}
