/**
 * 
 */
package org.codejargon.fluentjdbc.api.query.listen;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Information about what was executed - query + parameters. Parameters are not available for batch executions
 * 
 * @author bwc
 */
public interface QueryInfo {

    /**
     * @return the sql query which was executed
     */
    String sql();

    /**
     * @return optional parameters
     */
    Optional<List<Object>> params();

    /**
     * @return optional named parameters
     */
    Optional<Map<String, Object>> namedParams();

}
