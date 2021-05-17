package org.codejargon.fluentjdbc.internal.query;

import java.sql.SQLType;
import java.util.List;

public class SqlAndParams {
    private final String sql;
    private final List<Object> params;

    public SqlAndParams(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    String sql() {
        return sql;
    }

    List<Object> params() {
        return params;
    }
    
    /**
     * check whether parameters assume callable statement or prepared statement
     * 
     * @return true if at least one of parameters is out parameter
     */
    public boolean hasOutParameters() {
        return params.parallelStream().anyMatch(param -> param instanceof SQLType);
    }
}
