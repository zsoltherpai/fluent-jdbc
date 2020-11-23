/**
 * 
 */
package org.codejargon.fluentjdbc.internal.query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.query.listen.QueryInfo;

/**
 * @author tono
 *
 */
public class QueryInfoInternal implements QueryInfo {
    private final String sql;
    private final Optional<List<Object>> params;
    private final Optional<Map<String, Object>> namedParams;

    /**
     * @param sql
     * @param params
     * @param namedParams
     */
    public QueryInfoInternal(String sql, Optional<List<Object>> params, Optional<Map<String, Object>> namedParams) {
        this.sql = sql;
        this.params = params;
        this.namedParams = namedParams;
    }

    public static Optional<QueryInfoInternal> optional(String sql, List<Object> params, Map<String, Object> namedParams) {
        return Optional.of(of(sql, params, namedParams));
    }

    public static QueryInfoInternal of(String sql, List<Object> params, Map<String, Object> namedParams) {
        return new QueryInfoInternal(sql, params.isEmpty() ? Optional.empty() : Optional.of(params), 
                        namedParams.isEmpty() ? Optional.empty() : Optional.of(namedParams));
    }

    public static QueryInfoInternal of(String sql) {
        return new QueryInfoInternal(sql, Optional.empty(), Optional.empty());
    }

    /**
     * @return the sql
     */
    @Override
    public String sql() {
        return sql;
    }

    /**
     * @return the params
     */
    @Override
    public Optional<List<Object>> params() {
        return params;
    }

    /**
     * @return the namedParams
     */
    @Override
    public Optional<Map<String, Object>> namedParams() {
        return namedParams;
    }
}
