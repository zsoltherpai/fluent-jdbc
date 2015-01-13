package org.fluentjdbc.internal;

import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.fluentjdbc.api.ParamSetter;

public class DefaultParamSetters {

    private static final Map<Class, ParamSetter> setters;

    static {
        Map<Class, ParamSetter> ss = new HashMap<>();
        reg(ss, Date.class, (param, ps, i) -> {
            ps.setDate(i, new java.sql.Date(param.getTime()));
        });
        reg(ss, Instant.class, (param, ps, i) -> {
            ps.setTimestamp(i, timestamp(param));
        });
        reg(ss, OffsetDateTime.class, (param, ps, i) -> {
            ps.setTimestamp(i, timestamp(param.toInstant()));
        });
        reg(ss, ZonedDateTime.class, (param, ps, i) -> {
            ps.setTimestamp(i, timestamp(param.toInstant()));
        });
        reg(ss, LocalDate.class, (param, ps, i) -> {
            ps.setDate(i, java.sql.Date.valueOf(param));
        });
        reg(ss, LocalTime.class, (param, ps, i) -> {
            ps.setTime(i, java.sql.Time.valueOf(param));
        });
        reg(ss, LocalDateTime.class, (param, ps, i) -> {
            ps.setTimestamp(i, java.sql.Timestamp.valueOf(param));
        });
        reg(ss, Year.class, (param, ps, i) -> {
            ps.setDate(i, java.sql.Date.valueOf(LocalDate.of(param.getValue(), Month.JANUARY, 1)));
        });
        reg(ss, YearMonth.class, (param, ps, i) -> {
            ps.setDate(i, java.sql.Date.valueOf(LocalDate.of(param.getYear(), param.getMonth(), 1)));
        });
        setters = Collections.unmodifiableMap(ss);
    }

    public static Map<Class, ParamSetter> setters() {
        return setters;
    }

    static java.sql.Timestamp timestamp(Instant instant) {
        return java.sql.Timestamp.from(instant);
    }

    private static <T> void reg(
            Map<Class, ParamSetter> setters,
            Class<T> clazz,
            ParamSetter<T> setter
    ) {
        setters.put(clazz, setter);
    }
}
