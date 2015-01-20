package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.internal.query.namedparameter.TransformedSql;
import org.codejargon.fluentjdbc.internal.support.Ints;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedSqlAndParams;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

class BatchQueryInternal implements BatchQuery {
    private final String sql;
    private final QueryInternal query;
    private Optional<Iterator<List<Object>>> params = Optional.empty();
    private Optional<Iterator<Map<String, Object>>> namedParams = Optional.empty();
    private Optional<Integer> batchSize = Optional.empty();

    public BatchQueryInternal(String sql, QueryInternal query) {
        this.sql = sql;
        this.query = query;
    }

    @Override
    public BatchQuery params(Iterator<List<Object>> params) {
        Preconditions.checkNotNull(params, "params");
        Preconditions.checkArgument(!namedParams.isPresent(), "Positional parameters can't be set if named parameters are already set.");
        this.params = Optional.of(params);
        return this;
    }

    @Override
    public BatchQuery namedParams(Iterator<Map<String, Object>> namedParams) {
        Preconditions.checkNotNull(namedParams, "namedParams");
        Preconditions.checkArgument(!params.isPresent(), "Named parameters can't be set if positional parameters are already set.");
        this.namedParams = Optional.of(namedParams);
        return this;
    }

    @Override
    public BatchQuery batchSize(Integer batchSize) {
        Preconditions.checkNotNull(batchSize, "batch size");
        Preconditions.checkArgument(batchSize > 0, "batch size must be greater than 0");
        this.batchSize = Optional.of(batchSize);
        return this;
    }

    @Override
    public List<UpdateResult> run() {
        Preconditions.checkArgument(params.isPresent() || namedParams.isPresent(), "Parameters must be set to run a batch query");
        return params.isPresent() ? positional() : named();
    }

    private List<UpdateResult> positional() {
        return query.query(
                connection -> {
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        int i = 0;
                        List<UpdateResult> updateResults = new ArrayList<>();
                        while (params.get().hasNext()){
                            i = assignParamAndRunBatchWhenNeeded(statement, i, updateResults, params.get().next());
                        }
                        updateResults.addAll(runBatch(statement));
                        return Collections.unmodifiableList(updateResults);
                    }
                }, sql);
    }

    private List<UpdateResult> named() {
        return query.query(
                connection -> {
                    TransformedSql transformedSql = query.transformedSql(sql);
                    try (PreparedStatement statement = connection.prepareStatement(transformedSql.sql())) {
                        int i = 0;
                        List<UpdateResult> updateResults = new ArrayList<>();
                        while (namedParams.get().hasNext()){
                            Map<String, Object> namedParamElement = namedParams.get().next();
                            SqlAndParams sqlAndParams = NamedSqlAndParams.sqlAndParams(transformedSql, namedParamElement);
                            i = assignParamAndRunBatchWhenNeeded(statement, i, updateResults, sqlAndParams.params());
                        }
                        updateResults.addAll(runBatch(statement));
                        return Collections.unmodifiableList(updateResults);
                    }
                }, sql);
    }

    private int assignParamAndRunBatchWhenNeeded(PreparedStatement statement, int i, List<UpdateResult> updateResults, List<Object> params) throws SQLException {
        query.assignParams(statement, params);
        statement.addBatch();
        ++i;
        if (batchSize.isPresent() && i % batchSize.get() == 0) {
            updateResults.addAll(runBatch(statement));
        }
        return i;
    }

    private List<UpdateResult> runBatch(PreparedStatement statement) throws SQLException{
        List<Integer> updateds = Ints.asList(statement.executeBatch());
        return Collections.unmodifiableList(
                updateds.stream().map(i -> (long) i).map(UpdateResultInternal::new).collect(Collectors.toList())
        );
    }
}
