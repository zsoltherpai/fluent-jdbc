package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.ParamSetter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DefaultParamSetters {

    private static final Map<Class, ParamSetter> setters;

    static {
        Map<Class, ParamSetter> ss = new HashMap<>();
        javaDate(ss);
        javaTime(ss);
        javaBinary(ss);
        setters = Collections.unmodifiableMap(ss);
    }

    private static void javaTime(Map<Class, ParamSetter> ss) {
        reg(ss, Instant.class, (param, ps, i) -> ps.setTimestamp(i, timestamp(param)));
        reg(ss, OffsetDateTime.class, (param, ps, i) -> ps.setTimestamp(i, timestamp(param.toInstant())));
        reg(ss, ZonedDateTime.class, (param, ps, i) -> ps.setTimestamp(i, timestamp(param.toInstant())));
        reg(ss, LocalDate.class, (param, ps, i) -> ps.setDate(i, java.sql.Date.valueOf(param)));
        reg(ss, LocalTime.class, (param, ps, i) -> ps.setTime(i, java.sql.Time.valueOf(param)));
        reg(ss, LocalDateTime.class, (param, ps, i) -> ps.setTimestamp(i, java.sql.Timestamp.valueOf(param)));
        reg(ss, Year.class, (param, ps, i) -> ps.setDate(i, java.sql.Date.valueOf(LocalDate.of(param.getValue(), Month.JANUARY, 1))));
        reg(ss, YearMonth.class, (param, ps, i) -> ps.setDate(i, java.sql.Date.valueOf(LocalDate.of(param.getYear(), param.getMonth(), 1))));
    }

    private static void javaDate(Map<Class, ParamSetter> ss) {
        reg(ss, Date.class, (param, ps, i) -> {
            ps.setTimestamp(i, new java.sql.Timestamp(param.getTime()));
        });
    }

    private static void javaBinary(Map<Class, ParamSetter> ss) {
        reg(ss, byte[].class, (param, ps, i) -> ps.setBlob(i, new ByteArrayInputStream(param)));
    }

    static Map<Class, ParamSetter> setters() {
        return setters;
    }

    private static java.sql.Timestamp timestamp(Instant instant) {
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
