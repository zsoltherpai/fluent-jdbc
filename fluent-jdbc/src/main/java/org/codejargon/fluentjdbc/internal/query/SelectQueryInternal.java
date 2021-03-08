package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.codejargon.fluentjdbc.api.query.SqlConsumer;
import org.codejargon.fluentjdbc.api.query.SqlErrorHandler;
import org.codejargon.fluentjdbc.internal.support.Predicates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Optional.empty;
import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkArgument;
import static org.codejargon.fluentjdbc.internal.support.Preconditions.checkNotNull;

class SelectQueryInternal extends SingleQueryBase implements SelectQuery {

    private Predicate filter = Predicates.alwaysTrue();
    private Optional<Integer> fetchSize = empty();
    private Optional<Long> maxRows = empty();

    SelectQueryInternal(String sql, QueryInternal query) {
        super(query, sql);
    }

    @Override
    public <T> SelectQuery filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public SelectQuery fetchSize(Integer rows) {
        checkNotNull(rows, "rows");
        checkArgument(rows >= 0, "Fetch size rows must be >= 0");
        this.fetchSize = Optional.of(rows);
        return this;
    }

    @Override
    public SelectQuery maxRows(Long rows) {
        checkNotNull(rows, "rows");
        checkArgument(rows >= 0, "Max results rows must be >= 0");
        this.maxRows = Optional.of(rows);
        return this;
    }

    @Override
    public SelectQuery params(List<?> params) {
        addParameters(params);
        return this;
    }

    @Override
    public SelectQuery params(Object... params) {
        addParameters(params);
        return this;
    }

    @Override
    public SelectQuery namedParams(Map<String, ?> namedParams) {
        addNamedParameters(namedParams);
        return this;
    }

    @Override
    public SelectQuery namedParam(String name, Object parameter) {
        addNamedParameter(name, parameter);
        return this;
    }

    @Override
    public SelectQuery errorHandler(SqlErrorHandler sqlErrorHandler ) {
        this.sqlErrorHandler = () -> sqlErrorHandler;
        return this;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> firstResult(Mapper<T> mapper) {
        return runQuery(
                ps -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        Optional<T> result = empty();
                        while (rs.next() && !result.isPresent()) {
                            T candidate = mapper.map(rs);
                            if (filter.test(candidate)) {
                                result = Optional.ofNullable(candidate);
                            }
                        }
                        return result;
                    }
                },
                sqlErrorHandler.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T singleResult(Mapper<T> mapper) {
        Optional<T> firstResult = firstResult(mapper);
        if (!firstResult.isPresent()) {
            throw query.queryException(sql, Optional.of("At least one result expected"), empty());
        }
        return firstResult.get();
    }


    @Override
    public <T> List<T> listResult(Mapper<T> mapper) {
        List<T> results = new ArrayList<>();
        iterateResult(mapper, results::add);
        return Collections.unmodifiableList(results);
    }

    @Override
    public <T> Set<T> setResult(Mapper<T> mapper) {
        Set<T> results = new HashSet<>();
        iterateResult(mapper, results::add);
        return Collections.unmodifiableSet(results);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void iterateResult(Mapper<T> mapper, Consumer<T> consumer) {
        runQuery(
                ps -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            T candidate = mapper.map(rs);
                            if (filter.test(candidate)) {
                                consumer.accept(candidate);
                            }
                        }
                    }
                    return null;
                },
                sqlErrorHandler.get()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void iterateResult(SqlConsumer<ResultSet> consumer) {
        runQuery(
                ps -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            if(filter.test(rs)) {
                                consumer.accept(rs);
                            }
                        }
                    }
                    return null;
                },
                sqlErrorHandler.get());
    }

    @Override
    void customizeQuery(PreparedStatement statement, QueryConfig config) throws SQLException {
        selectFetchSize(statement, config);
        maxResults(statement);
    }

    private void selectFetchSize(PreparedStatement statement, QueryConfig config) throws SQLException {
        Optional<Integer> activeFetchSize = config.fetchSize(fetchSize);
        if (activeFetchSize.isPresent()) {
            statement.setFetchSize(activeFetchSize.get());
        }
    }

    private void maxResults(PreparedStatement statement) throws SQLException {
        if (maxRows.isPresent()) {
            if (maxRows.get() > Integer.MAX_VALUE) {
                setLargeMaxRows(statement);
            } else {
                statement.setMaxRows((int) maxRows.get().longValue());
            }
        }
    }

    private void setLargeMaxRows(PreparedStatement statement) throws SQLException {
        try {
            statement.setLargeMaxRows(maxRows.get());
        } catch (SQLException e) {
            throw new FluentJdbcException(
                    String.format(
                            "The JDBC driver %s doesn't support setLargeMaxRows(). Set max results <= Integer.MAX_VALUE",
                            statement.getConnection().getMetaData().getDriverName())
            );
        }
    }
}
