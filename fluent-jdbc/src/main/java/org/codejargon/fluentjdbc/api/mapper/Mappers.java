package org.codejargon.fluentjdbc.api.mapper;

import org.codejargon.fluentjdbc.api.query.Mapper;

/**
 * A set of common mappers for convenience
 */
public abstract class Mappers {
    private static final Mapper<Integer> singleInteger = (rs) -> rs.getInt(1);
    private static final Mapper<Long> singleLong = (rs) -> rs.getLong(1);
    private static final Mapper<String> singleString = (rs) -> rs.getString(1);

    /**
     * Maps the first Integer column.
     * @return first Integer column
     */
    public static Mapper<Integer> singleInteger() {
        return singleInteger;
    }

    /**
     * Maps the first Long column.
     * @return first Long column
     */
    public static Mapper<Long> singleLong() {
        return singleLong;
    }

    /**
     * Maps the first string column
     * @return first string column
     */
    public static Mapper<String> singleString() {
        return singleString;
    }
}
