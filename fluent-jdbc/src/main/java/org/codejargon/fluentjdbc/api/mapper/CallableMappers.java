package org.codejargon.fluentjdbc.api.mapper;

import java.math.BigDecimal;

import org.codejargon.fluentjdbc.api.query.CallableMapper;

/**
 * <p>A set of common mappers for convenience.</p>
 * @see org.codejargon.fluentjdbc.api.mapper.ObjectMappers
 */
public abstract class CallableMappers {
    private static final CallableMapper<Integer> singleInteger = (cs) -> cs.getInt(1);
    private static final CallableMapper<Long> singleLong = (cs) -> cs.getLong(1);
    private static final CallableMapper<String> singleString = (cs) -> cs.getString(1);
    private static final CallableMapper<BigDecimal> singleBigDecimal = (cs) -> cs.getBigDecimal(1);
    private static final CallableMapper<Boolean> singleBoolean = (cs) -> cs.getBoolean(1);

    /**
     * Maps the first Integer column.
     * @return first Integer column
     */
    public static CallableMapper<Integer> singleInteger() {
        return singleInteger;
    }

    /**
     * Maps the first Long column.
     * @return first Long column
     */
    public static CallableMapper<Long> singleLong() {
        return singleLong;
    }

    /**
     * Maps the first string column
     * @return first string column
     */
    public static CallableMapper<String> singleString() {
        return singleString;
    }

    /**
     * Maps the first BigDecimal column
     * @return first BigDecimal column
     */
    public static CallableMapper<BigDecimal> singleBigDecimal() {
        return singleBigDecimal;
    }

    /**
     * Maps the first Boolean column
     * @return first Boolean column
     */
    public static CallableMapper<Boolean> singleBoolean() {
        return singleBoolean;
    }
}
