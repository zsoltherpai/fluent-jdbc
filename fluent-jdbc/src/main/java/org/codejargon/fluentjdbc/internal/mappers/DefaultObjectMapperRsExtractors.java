package org.codejargon.fluentjdbc.internal.mappers;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codejargon.fluentjdbc.api.mapper.ObjectMapperRsExtractor;

public class DefaultObjectMapperRsExtractors {

    private static final Map<Class, ObjectMapperRsExtractor> extractors;

    static {
        Map<Class, ObjectMapperRsExtractor<?>> exs = new HashMap<>();
        basicTypes(exs);
        javaDate(exs);
        javaTimeTypes(exs);
        binaryTypes(exs, Boolean.parseBoolean(System.getProperty("preferDirect", "false")));
        extractors = Collections.unmodifiableMap(exs);
    }

    private static void javaDate(Map<Class, ObjectMapperRsExtractor<?>> exs) {
        reg(exs, java.util.Date.class, ResultSet::getTimestamp);
    }

    private static void javaTimeTypes(Map<Class, ObjectMapperRsExtractor<?>> exs) {
        reg(exs, LocalDate.class, (rs, i) -> {
            Date date = rs.getDate(i);
            return date != null ? date.toLocalDate() : null;
        });
        reg(exs, LocalDateTime.class, (rs, i) -> {
            Timestamp stamp = rs.getTimestamp(i);
            return stamp != null ? stamp.toLocalDateTime() : null;
        });
        reg(exs, LocalTime.class, (rs, i) -> {
            Time time = rs.getTime(i);
            return time != null ? time.toLocalTime() : null;
        });
        reg(exs, Year.class, (rs, i) -> {
            Date date = rs.getDate(i);
            return date != null ? Year.from(date.toLocalDate()) : null;
        });
        reg(exs, YearMonth.class, (rs, i) -> {
            Date date = rs.getDate(i);
            return date != null ? YearMonth.from(date.toLocalDate()) : null;
        });
        reg(exs, Instant.class, (rs, i) -> {
            Timestamp stamp = rs.getTimestamp(i);
            return stamp != null ? stamp.toInstant() : null;
        });
    }

    private static void basicTypes(Map<Class, ObjectMapperRsExtractor<?>> exs) {
        reg(exs, Boolean.class, ResultSet::getBoolean);
        reg(exs, boolean.class, ResultSet::getBoolean);
        reg(exs, Short.class, ResultSet::getShort);
        reg(exs, short.class, ResultSet::getShort);
        reg(exs, Integer.class, ResultSet::getInt);
        reg(exs, int.class, ResultSet::getInt);
        reg(exs, Long.class, ResultSet::getLong);
        reg(exs, long.class, ResultSet::getLong);
        reg(exs, Float.class, ResultSet::getFloat);
        reg(exs, float.class, ResultSet::getFloat);
        reg(exs, Double.class, ResultSet::getDouble);
        reg(exs, double.class, ResultSet::getDouble);
        reg(exs, BigDecimal.class, ResultSet::getBigDecimal);
        reg(exs, Timestamp.class, ResultSet::getTimestamp);
        reg(exs, Time.class, ResultSet::getTime);
        reg(exs, Date.class, ResultSet::getDate);
        reg(exs, String.class, ResultSet::getString);
    }

    private static void binaryTypes(Map<Class, ObjectMapperRsExtractor<?>> exs, boolean preferDirect) {
        reg(exs, byte[].class, (rs, i) -> {
            Blob blob = rs.getBlob(i);
            if (blob == null) {
                return null;
            }
            byte[] data = blob.getBytes(0, (int) blob.length());
            try {
              blob.free();
            } catch (Exception e) {
              // it's not JDBC 4.0 compatible -> ignore
            }
            return data;
        });
        reg(exs, ByteBuffer.class, (rs, i) -> {
            Blob blob = rs.getBlob(i);
            if (blob == null) {
                return null;
            }
            ByteBuffer data;
            if (preferDirect){
              data = ByteBuffer.allocateDirect((int) blob.length());
              data.put(blob.getBytes(0, data.capacity()));
              data.flip();
            } else {
              data = ByteBuffer.wrap(blob.getBytes(0, (int) blob.length()));
            }
            try {
              blob.free();
            } catch (Exception e) {
              // it's not JDBC 4.0 compatible -> ignore
            }
            return data;
        });
    }

    public static Map<Class, ObjectMapperRsExtractor> extractors() {
        return extractors;
    }

    private static <T> void reg(
            Map<Class, ObjectMapperRsExtractor<?>> exs,
            Class<T> clazz,
            ObjectMapperRsExtractor<T> extractor
    ) {
        exs.put(clazz, extractor);
    }
}
