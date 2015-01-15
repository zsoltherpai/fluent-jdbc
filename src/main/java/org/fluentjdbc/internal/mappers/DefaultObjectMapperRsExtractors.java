package org.fluentjdbc.internal.mappers;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.fluentjdbc.api.mapper.ObjectMapperRsExtractor;

public class DefaultObjectMapperRsExtractors {

    private static final Map<Class, ObjectMapperRsExtractor> extractors;

    static {
        Map<Class, ObjectMapperRsExtractor<?>> exs = new HashMap<>();
        basicTypes(exs);
        javaDate(exs);
        javaTimeTypes(exs);
        extractors = Collections.unmodifiableMap(exs);
    }

    private static void javaDate(Map<Class, ObjectMapperRsExtractor<?>> exs) {
        reg(exs, java.util.Date.class, ResultSet::getDate);
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
