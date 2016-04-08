package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;
import org.codejargon.fluentjdbc.internal.support.Ints;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.internal.query.namedparameter.SqlAndParamsForNamed;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static org.codejargon.fluentjdbc.internal.support.Sneaky.consumer;
import static org.codejargon.fluentjdbc.internal.support.Iterables.stream;

class BatchQueryInternal implements BatchQuery {
    private static final String namedSet = "Named parameters are already set.";
    private static final String positionalSet = "Positional parameters are already set.";

    private final String sql;
    private final QueryInternal query;
    private Optional<Iterator<? extends Collection<?>>> params = empty();
    private Optional<Iterator<Map<String, ?>>> namedParams = empty();
    private Optional<Integer> batchSize = empty();

    public BatchQueryInternal(String sql, QueryInternal query) {
        this.sql = sql;
        this.query = query;
    }

    @Override
    public <C extends Collection<?>> BatchQuery params(Iterator<C> params) {
        Preconditions.checkNotNull(params, "params");
        Preconditions.checkArgument(!this.params.isPresent(), positionalSet);
        Preconditions.checkArgument(!namedParams.isPresent(), namedSet);
        this.params = Optional.of(params);
        return this;
    }

    @Override
    public <C extends Collection<?>> BatchQuery params(Iterable<C> params) {
        return params(params.iterator());
    }

    @Override
    public <C extends Collection<?>> BatchQuery params(Stream<C> params) {
        return params(params.iterator());
    }

    @Override
    public BatchQuery namedParams(Iterator<Map<String, ?>> namedParams) {
        Preconditions.checkNotNull(namedParams, "namedParams");
        Preconditions.checkArgument(!this.namedParams.isPresent(), namedSet);
        Preconditions.checkArgument(!params.isPresent(), positionalSet);
        this.namedParams = Optional.of(namedParams);
        return this;
    }

    @Override
    public BatchQuery namedParams(Iterable<Map<String, ?>> params) {
        return namedParams(params.iterator());
    }

    @Override
    public BatchQuery namedParams(Stream<Map<String, ?>> params) {
        return namedParams(params.iterator());
    }

    @Override
    public BatchQuery batchSize(Integer batchSize) {
        Preconditions.checkNotNull(batchSize, "batch size");
        Preconditions.checkArgument(batchSize > 0, "batch size must be greater than 0");
        this.batchSize = Optional.of(batchSize);
        return this;
    }

    @Override
    public Collection<UpdateResult> run() {
        Preconditions.checkArgument(params.isPresent() || namedParams.isPresent(), "Parameters must be set to run a batch query");
        return query.query(
                connection -> params.isPresent() ? positional(connection) : named(connection),
                Optional.of(sql)
        );
    }

    private Collection<UpdateResult> positional(Connection connection) throws SQLException {
        try (PreparedStatement statement = query.preparedStatementFactory.createBatch(connection, sql)) {
            return runBatches(statement, stream(params.get()));
        }

    }

    private Collection<UpdateResult> named(Connection connection) throws SQLException {
        NamedTransformedSql namedTransformedSql = query.config.namedTransformedSql(sql);
        try (PreparedStatement statement = query.preparedStatementFactory.createBatch(connection, namedTransformedSql.sql())) {
            return runBatches(
                    statement,
                    stream(namedParams.get()).map(
                            namedParam -> SqlAndParamsForNamed.params(namedTransformedSql.parsedSql(), namedParam)
                    )
            );
        }
    }

    private Collection<UpdateResult> runBatches(PreparedStatement ps, Stream<? extends Collection<?>> params) throws SQLException {
        BatchExecution batchExecution = new BatchExecution(ps);
        params.forEachOrdered(consumer(batchExecution::add));
        return batchExecution.results();
    }

    private class BatchExecution {
        private final PreparedStatement ps;
        private final Collection<UpdateResult> updateResults = new ArrayList<>();
        private long totalBatchesAdded = 0L;
        private boolean newAdded = false;


        public BatchExecution(PreparedStatement ps) {
            this.ps = ps;
        }

        public void add(Collection<?> params) throws SQLException {
            addParamsToBatch(params);
            if (batchSize.isPresent() && totalBatchesAdded % batchSize.get() == 0) {
                runBatch();
            }
        }

        private void addParamsToBatch(Collection<?> params) throws SQLException {
            query.assignParams(ps, params);
            ps.addBatch();
            ++totalBatchesAdded;
            newAdded = true;
        }

        private void runBatch() throws SQLException {
            updateResults.addAll(
                    Ints.asList(ps.executeBatch()).stream()
                            .map(i -> (long) i)
                            .map(UpdateResultInternal::new)
                            .collect(Collectors.toList())
            );
            ps.clearBatch();
            newAdded = false;
        }

        private Collection<UpdateResult> results() throws SQLException {
            if(newAdded) {
                runBatch();
            }
            return Collections.unmodifiableCollection(updateResults);
        }
    }
}
