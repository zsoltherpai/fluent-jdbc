package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.PreparedStatement;
import java.util.*;

abstract class SingleQueryBase {
    protected final String sql;
    protected final QueryInternal query;
    protected final List<Object> params = new ArrayList<>(0);
    protected final Map<String, Object> namedParams = new HashMap<>(0);

    protected SingleQueryBase(QueryInternal query, String sql) {
        this.query = query;
        this.sql = sql;
    }

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

    protected <T> T runQuery(QueryRunnerPreparedStatement<T> queryRunnerPreparedStatement) {
        return query.query(connection -> {
            try (PreparedStatement ps = query.preparedStatement(connection, querySpecs())) {
                return queryRunnerPreparedStatement.run(ps);
            }
        }, sql);
    }
    
    protected abstract SingleQuerySpecification querySpecs();
    
    
    
    
}
