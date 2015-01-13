package org.fluentjdbc.internal.mappers;

import java.math.BigDecimal;
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
import org.fluentjdbc.api.mapper.ObjectMapperRsExtractor;

public class DefaultObjectMapperRsExtractors {

    private static final Map<Class, ObjectMapperRsExtractor> extractors;

    static {
        Map<Class, ObjectMapperRsExtractor<?>> exs = new HashMap<>();
        reg(exs, Boolean.class, ResultSet::getBoolean);
        reg(exs, boolean.class, ResultSet::getBoolean);
        reg(exs, Short.class, ResultSet::getShort);
        reg(exs, short.class, ResultSet::getShort);
        reg(exs, Integer.class, ResultSet::getInt);
        reg(exs, int.class, ResultSet::getInt);
        reg(exs, Long.class, (resultSet, columnIndex) -> {
            return resultSet.getLong(columnIndex);
        });
        reg(exs, long.class, ResultSet::getLong);
        reg(exs, Float.class, ResultSet::getFloat);
        reg(exs, float.class, ResultSet::getFloat);
        reg(exs, Double.class, ResultSet::getDouble);
        reg(exs, double.class, ResultSet::getDouble);
        reg(exs, BigDecimal.class, ResultSet::getBigDecimal);
        reg(exs, Timestamp.class, ResultSet::getTimestamp);
        reg(exs, Time.class, ResultSet::getTime);
        reg(exs, java.sql.Date.class, ResultSet::getDate);
        reg(exs, String.class, ResultSet::getString);
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
        extractors = Collections.unmodifiableMap(exs);
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
