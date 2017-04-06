package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class FetchGenKey<T> {
    final Optional<Mapper<T>> mapper;

    static FetchGenKey<Void> no() {
        return new FetchGenKey<Void>(Optional.empty());
    }

    static <T> FetchGenKey<T> yes(Mapper<T> mapper) {
        return new FetchGenKey<T>(Optional.of(mapper));
    }

    private FetchGenKey(Optional<Mapper<T>> mapper) {
        this.mapper = mapper;
    }

    boolean fetch() {
        return mapper.isPresent();
    }

    UpdateResultGenKeys<T> genKeys(PreparedStatement ps, Integer affected) throws SQLException {
        List<T> keys = generatedKeys(ps);
        if(keys.size() != affected) {
            throw new FluentJdbcException("Can't fetch generated keys properly, does the jdbc driver support it?");
        }
        return new UpdateResultGenKeysInternal<>((long) affected, keys);
    }

    private List<T> generatedKeys(PreparedStatement ps) throws SQLException {
        try (ResultSet keySet = ps.getGeneratedKeys()) {
            List<T> keys = new ArrayList<>(1);
            while (keySet.next()) {
                keys.add(mapper.get().map(keySet));
            }
            return Collections.unmodifiableList(keys);
        }
    }
}
