package org.codejargon.fluentjdbc.api.mapper;

import org.codejargon.fluentjdbc.api.query.Mapper;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private static final Mapper<Map<String, Object>> map = rs -> {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> result = new HashMap<>(meta.getColumnCount());
        for(int column = 1; column <= meta.getColumnCount(); ++column) {
            result.put(meta.getColumnLabel(column), rs.getObject(column));
        }
        return Collections.unmodifiableMap(result);
    };
    private static final Mapper<byte[]> singleByteArray = (rs) -> {
        Blob blob = rs.getBlob(1);
        return blob.getBytes(1, (int) blob.length());
    };


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

    /**
     *
     * @return
     */
    public static Mapper<Map<String, Object>> map() {
        return map;
    }

    public static Mapper<byte[]> singleByteArray() {
        return singleByteArray;
    }
}
