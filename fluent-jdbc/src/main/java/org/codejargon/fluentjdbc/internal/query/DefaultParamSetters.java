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
        try {
          reg(ss, Class.forName("java.nio.DirectByteBuffer"), (p, ps, i) -> {
              ByteBuffer param = (ByteBuffer)p;
              if (param.hasArray()) {
                  ps.setBlob(i, new ByteArrayInputStream(param.array()));
              }else{
                  ps.setBlob(i, new ByteBufferInputStream(param));
              }
          });
          reg(ss, Class.forName("java.nio.HeapByteBuffer"), (p, ps, i) -> {
            ByteBuffer param = (ByteBuffer)p;
            if (param.hasArray()) {
                ps.setBlob(i, new ByteArrayInputStream(param.array()));
            }else{
                ps.setBlob(i, new ByteBufferInputStream(param));
            }
        });
        } catch (ClassNotFoundException e) {
          // ByteBuffer implementations missing
        }
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

    private static class ByteBufferInputStream extends InputStream {
        private final ByteBuffer buffer;

        ByteBufferInputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        public int read() throws IOException {
            if (!buffer.hasRemaining()) return -1;
            return buffer.get() & 0xFF;
        }

        public int read(byte[] bytes, int offset, int length) throws IOException {
            if (length == 0) return 0;
            int count = Math.min(buffer.remaining(), length);
            if (count == 0) return -1;
            buffer.get(bytes, offset, count);
            return count;
        }

        public int available() throws IOException {
            return buffer.remaining();
        }
    }
}
