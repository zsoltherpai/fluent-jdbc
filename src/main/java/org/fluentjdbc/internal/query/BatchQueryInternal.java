package org.fluentjdbc.internal.query;

import org.fluentjdbc.api.query.BatchQuery;
import org.fluentjdbc.api.query.UpdateResult;
import org.fluentjdbc.internal.support.Ints;
import org.fluentjdbc.internal.support.Preconditions;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

class BatchQueryInternal implements BatchQuery {
    private final String sql;
    private final QueryInternal query;
    private Iterator<List<Object>> params = (new ArrayList<List<Object>>()).iterator();
    private Optional<Integer> batchSize = Optional.empty();

    public BatchQueryInternal(String sql, QueryInternal query) {
        this.sql = sql;
        this.query = query;
    }

    @Override
    public BatchQuery params(Iterator<List<Object>> params) {
        Preconditions.checkNotNull(params, "params");
        this.params = params;
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
        return query.query(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int i = 0;
                List<UpdateResult> updateResults = new ArrayList<>();
                while (params.hasNext()){
                    query.assignParams(statement, params.next());
                    statement.addBatch();
                    ++i;
                    if (batchSize.isPresent() && i % batchSize.get() == 0) {
                        updateResults.addAll(runBatch(statement));
                    }
                }
                updateResults.addAll(runBatch(statement));
                return Collections.unmodifiableList(updateResults);
            }
        }, sql);
    }

    private List<UpdateResult> runBatch(PreparedStatement statement) throws SQLException{
        List<Integer> updateds = Ints.asList(statement.executeBatch());
        return Collections.unmodifiableList(
                updateds.stream().map(i -> (long) i).map(UpdateResultInternal::new).collect(Collectors.toList())
        );
    }
}
