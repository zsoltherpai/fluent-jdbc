package org.codejargon.fluentjdbc.api.mapper;

import org.codejargon.fluentjdbc.api.query.Mapper;

import java.math.BigDecimal;

/**
 * <p>A set of common mappers for convenience.</p>
 * @see org.codejargon.fluentjdbc.api.mapper.ObjectMappers
 */
public abstract class Mappers {
    private static final Mapper<Integer> singleInteger = (rs) -> rs.getInt(1);
    private static final Mapper<Long> singleLong = (rs) -> rs.getLong(1);
    private static final Mapper<String> singleString = (rs) -> rs.getString(1);
    private static final Mapper<BigDecimal> singleBigDecimal = (rs) -> rs.getBigDecimal(1);
    private static final Mapper<Boolean> singleBoolean = (rs) -> rs.getBoolean(1);

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

    /**
     * Maps the first BigDecimal column
     * @return first BigDecimal column
     */
    public static Mapper<BigDecimal> singleBigDecimal() {
        return singleBigDecimal;
    }

    /**
     * Maps the first Boolean column
     * @return first Boolean column
     */
    public static Mapper<Boolean> singleBoolean() {
        return singleBoolean;
    }
}
