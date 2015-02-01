package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.internal.query.namedparameter.TransformedSql;
import org.codejargon.fluentjdbc.internal.support.Ints;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedSqlAndParams;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.Connection;
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
        return query.query(
                connection -> params.isPresent() ? positional(connection) : named(connection),
                sql
        );
    }

    private List<UpdateResult> positional(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            Batch batch = new Batch();
            while (params.get().hasNext()) {
                assignParamAndRunBatchWhenNeeded(statement, batch, params.get().next());
            }
            runBatch(statement, batch);
            return batch.results();
        }

    }

    private List<UpdateResult> named(Connection connection) throws SQLException {
        TransformedSql transformedSql = query.config.transformedSql(sql);
        try (PreparedStatement statement = connection.prepareStatement(transformedSql.sql())) {
            Batch batch = new Batch();
            while (namedParams.get().hasNext()) {
                SqlAndParams sqlAndParams = NamedSqlAndParams.sqlAndParams(transformedSql, namedParams.get().next());
                assignParamAndRunBatchWhenNeeded(statement, batch, sqlAndParams.params());
            }
            runBatch(statement, batch);
            return batch.results();
        }
    }

    private void assignParamAndRunBatchWhenNeeded(PreparedStatement statement, Batch batch, List<Object> params) throws SQLException {
        query.assignParams(statement, params);
        statement.addBatch();
        batch.added();
        if (batchSize.isPresent() && batch.batchesAdded % batchSize.get() == 0) {
            runBatch(statement, batch);
        }
    }

    private void runBatch(PreparedStatement statement, Batch batch) throws SQLException {
        List<Integer> updateds = Ints.asList(statement.executeBatch());
        batch.newResults(
                updateds.stream().map(i -> (long) i).map(UpdateResultInternal::new).collect(Collectors.toList())
        );
    }
    
    private static class Batch {
        private int batchesAdded = 0;
        private final List<UpdateResult> updateResults = new ArrayList<>();
        
        private void added() {
            ++batchesAdded;
        }
        
        private void newResults(List<UpdateResult> newResults) {
            updateResults.addAll(newResults);
        }
        
        private List<UpdateResult> results() {
            return Collections.unmodifiableList(updateResults);
        }
    }
}
