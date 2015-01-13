package org.fluentjdbc.internal.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.fluentjdbc.api.query.Mapper;
import org.fluentjdbc.api.query.SelectQuery;
import org.fluentjdbc.internal.support.Predicates;

class SelectQueryInternal extends SingleQueryBase implements SelectQuery {

    private final QueryInternal query;
    private final String sql;

    private Predicate filter = Predicates.alwaysTrue();

    SelectQueryInternal(String sql, QueryInternal query) {
        super();
        this.sql = sql;
        this.query = query;
    }

    @Override
    public <T> SelectQuery filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public SelectQuery params(List<Object> params) {
        addParameters(params);
        return this;
    }

    @Override
    public SelectQuery params(Object... params) {
        addParameters(params);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> firstResult(Mapper<T> mapper) {
        return query.query(connection -> {
            try (PreparedStatement ps = query.preparedStatement(connection, sql, params);
                 ResultSet rs = ps.executeQuery();
            ) {
                Optional<T> result = Optional.empty();
                while (rs.next() && !result.isPresent()) {
                    T candidate = mapper.map(rs);
                    if (filter.test(candidate)) {
                        result = Optional.of(candidate);
                    }
                }
                return result;
            }
        }, sql);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T singleResult(Mapper<T> mapper) {
        Optional<T> firstResult = firstResult(mapper);
        if(!firstResult.isPresent()) {
            throw query.queryException(sql, Optional.of("At least one result expected"), Optional.empty());
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
        query.query(connection -> {
            try (PreparedStatement ps = query.preparedStatement(connection, sql, params);
                 ResultSet rs = ps.executeQuery();
            ) {
                while (rs.next()) {
                    T candidate = mapper.map(rs);
                    if (filter.test(candidate)) {
                        consumer.accept(candidate);
                    }
                }

            }
            return null;
        }, sql);
    }


}
