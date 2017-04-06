package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.BatchQuery;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSqlFactory;
import org.codejargon.fluentjdbc.internal.support.Ints;
import org.codejargon.fluentjdbc.api.query.UpdateResult;
import org.codejargon.fluentjdbc.internal.query.namedparameter.SqlAndParamsForNamed;
import org.codejargon.fluentjdbc.internal.support.Preconditions;

import java.awt.geom.AffineTransform;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.codejargon.fluentjdbc.internal.support.Sneaky.consumer;
import static org.codejargon.fluentjdbc.internal.support.Iterables.stream;

class BatchQueryInternal implements BatchQuery {
    private static final String namedSet = "Named parameters are already set.";
    private static final String positionalSet = "Positional parameters are already set.";

    private final String sql;
    private final QueryInternal query;
    private Optional<Iterator<List<?>>> params = empty();
    private Optional<Iterator<Map<String, ?>>> namedParams = empty();
    private Optional<Integer> batchSize = empty();

    public BatchQueryInternal(String sql, QueryInternal query) {
        this.sql = sql;
        this.query = query;
    }

    @Override
    public BatchQuery params(Iterator<List<?>> params) {
        Preconditions.checkNotNull(params, "params");
        Preconditions.checkArgument(!this.params.isPresent(), positionalSet);
        Preconditions.checkArgument(!namedParams.isPresent(), namedSet);
        this.params = Optional.of(params);
        return this;
    }

    @Override
    public BatchQuery params(Iterable<List<?>> params) {
        return params(params.iterator());
    }

    @Override
    public BatchQuery params(Stream<List<?>> params) {
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
    public List<UpdateResult> run() {
        return run(FetchGenKey.no());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<UpdateResultGenKeys<T>> runFetchGenKeys(Mapper<T> generatedKeyMapper) {
        return Collections.unmodifiableList(
                run(FetchGenKey.yes(generatedKeyMapper)).stream().map(res -> (UpdateResultGenKeys<T>) res).collect(toList())
        );
    }

    private <T> List<UpdateResult> run(FetchGenKey<T> fetchGen) {
        Preconditions.checkArgument(params.isPresent() || namedParams.isPresent(), "Parameters must be set to run a batch query");
        return query.query(
                connection -> params.isPresent() ? positional(connection, fetchGen) : named(connection, fetchGen),
                Optional.of(sql)
        );
    }

    private <T> List<UpdateResult> positional(Connection connection, FetchGenKey<T> fetchGen) throws SQLException {
        try (PreparedStatement statement = query.preparedStatementFactory.createBatch(connection, fetchGen.fetch(), sql)) {
            BatchExecution<T> batchExecution = new BatchExecution<>(statement, fetchGen);
            stream(params.get()).forEachOrdered(consumer(batchExecution::add));
            return batchExecution.results();
        }

    }

    private <T> List<UpdateResult> named(Connection connection, FetchGenKey<T> fetchGen) throws SQLException {
        Iterator<Map<String, ?>> namedParamsIt = namedParams.get();
        if (!namedParamsIt.hasNext()) {
            return Collections.emptyList();
        } else {
            Map<String, ?> params = namedParamsIt.next();
            noCollectionsAllowed(params);
            NamedTransformedSql namedTransformedSql = query.config.namedTransformedSqlFactory.create(sql, params);
            try (PreparedStatement ps = query.preparedStatementFactory.createBatch(connection, fetchGen.fetch(), namedTransformedSql.sql())) {
                BatchExecution<T> batchExecution = new BatchExecution<>(ps, fetchGen);
                batchExecution.add(SqlAndParamsForNamed.params(namedTransformedSql.parsedSql(), params));
                while (namedParamsIt.hasNext()) {
                    params = namedParamsIt.next();
                    noCollectionsAllowed(params);
                    batchExecution.add(SqlAndParamsForNamed.params(namedTransformedSql.parsedSql(), params));
                }
                return batchExecution.results();
            }
        }
    }

    private void noCollectionsAllowed(Map<String, ?> namedParams) {
        if (NamedTransformedSqlFactory.hasCollection(namedParams)) {
            throw new FluentJdbcException("Batch updates should not contain collections as parameters");
        }
    }


    private class BatchExecution<T> {
        private final PreparedStatement ps;
        private final FetchGenKey<T> fetchGen;
        private final List<UpdateResult> updateResults = new ArrayList<>(0);
        private long totalBatchesAdded = 0L;
        private boolean newAdded = false;


        public BatchExecution(PreparedStatement ps, FetchGenKey<T> fetchGen) {
            this.ps = ps;
            this.fetchGen = fetchGen;
        }

        public void add(List<?> params) throws SQLException {
            addParamsToBatch(params);
            if (batchSize.isPresent() && totalBatchesAdded % batchSize.get() == 0) {
                runBatch();
            }
        }

        private void addParamsToBatch(List<?> params) throws SQLException {
            query.assignParams(ps, params);
            ps.addBatch();
            ++totalBatchesAdded;
            newAdded = true;
        }

        private void runBatch() throws SQLException {
            updateResults.addAll(
                    fetchGen.fetch() ?
                            executeFetch() :
                            executeNoFetch()
            );
        }

        private List<UpdateResultGenKeys<T>> executeFetch() throws SQLException {
            List<Integer> affected = Ints.asList(ps.executeBatch());
            Integer sum = affected.stream().mapToInt(aff -> aff).sum();
            UpdateResultGenKeys<T> genKeys = fetchGen.genKeys(ps, sum);
            Iterator<T> keys = genKeys.generatedKeys().iterator();
            return Collections.unmodifiableList(
                    affected.stream().map(
                        aff -> new UpdateResultGenKeysInternal<T>((long) aff, iterate(keys, aff))
                    ).collect(toList())
            );

        }

        private List<UpdateResultInternal> executeNoFetch() throws SQLException {
            return Ints.asList(ps.executeBatch()).stream()
                    .map(i -> (long) i)
                    .map(UpdateResultInternal::new)
                    .collect(toList());
        }

        private List<UpdateResult> results() throws SQLException {
            if (newAdded) {
                runBatch();
            }
            return Collections.unmodifiableList(updateResults);
        }


        private List<T> iterate(Iterator<T> keys, Integer num) {
            List<T> items = new ArrayList<>(num);
            IntStream.range(0, num).forEach(i -> items.add(keys.next()));
            return items;
        }
    }


}
