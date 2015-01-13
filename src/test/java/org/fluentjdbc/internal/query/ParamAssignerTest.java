package org.fluentjdbc.internal.query;

import org.fluentjdbc.internal.DefaultParamSetters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ParamAssignerTest {
    static LocalDateTime localDateTime = LocalDateTime.of(2015, Month.MARCH, 5, 12, 5);
    static LocalDate localDate = LocalDate.of(2015, Month.MARCH, 5);
    static LocalTime localTime = LocalTime.of(22, 12);
    static Instant instant = localDateTime.toInstant(ZoneOffset.MIN);
    static java.util.Date javaDate = new java.util.Date(Date.valueOf(localDate).getTime());

    static String string = "a";
    static Long longParam = 5L;
    static Integer intParam = 123;
    static BigDecimal bigDecimal = BigDecimal.TEN;
    static java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
    static Time time = Time.valueOf(localTime);
    static Timestamp timestamp = Timestamp.valueOf(localDateTime);



    @Mock
    PreparedStatement statement;
    ParamAssigner paramAssigner = new ParamAssigner(DefaultParamSetters.setters());

    // TODO dry
    @Test
    public void jdbcTypes() throws SQLException {
        paramAssigner.assignParams(
                statement,
                params(string, longParam, intParam, bigDecimal, sqlDate, time, timestamp)
        );
        verify(statement).setObject(1, string);
        verify(statement).setObject(2, longParam);
        verify(statement).setObject(3, intParam);
        verify(statement).setObject(4, bigDecimal);
        verify(statement).setObject(5, sqlDate);
        verify(statement).setObject(6, time);
        verify(statement).setObject(7, timestamp);
    }

    // TODO dry
    @Test
    public void javaTimeLocals() throws SQLException {
        paramAssigner.assignParams(
                statement,
                params(localDateTime, localDate, localTime)
        );
        ArgumentCaptor<Timestamp> localDateTimeCaptor = ArgumentCaptor.forClass(Timestamp.class);
        ArgumentCaptor<java.sql.Date> localDateCaptor = ArgumentCaptor.forClass(java.sql.Date.class);
        ArgumentCaptor<Time> localTimeCaptor = ArgumentCaptor.forClass(Time.class);

        ArgumentCaptor<Integer> i1 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> i2 = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> i3 = ArgumentCaptor.forClass(Integer.class);


        verify(statement).setTimestamp(i1.capture(), localDateTimeCaptor.capture());
        verify(statement).setDate(i2.capture(), localDateCaptor.capture());
        verify(statement).setTime(i3.capture(), localTimeCaptor.capture());

        assertEquals(timestamp, localDateTimeCaptor.getValue());
        assertEquals(sqlDate, localDateCaptor.getValue());
        assertEquals(time, localTimeCaptor.getValue());

        assertEquals(Integer.valueOf(1), i1.getValue());
        assertEquals(Integer.valueOf(2), i2.getValue());
        assertEquals(Integer.valueOf(3), i3.getValue());
    }

    // TODO dry
    @Test
    public void javaTimeInstant() throws SQLException {
        paramAssigner.assignParams(
                statement,
                params(instant)
        );
        ArgumentCaptor<Timestamp> instantCaptor = ArgumentCaptor.forClass(Timestamp.class);
        ArgumentCaptor<Integer> i1 = ArgumentCaptor.forClass(Integer.class);
        verify(statement).setTimestamp(i1.capture(), instantCaptor.capture());

        assertEquals(Timestamp.from(instant), instantCaptor.getValue());
        assertEquals(Integer.valueOf(1), i1.getValue());

    }

    // TODO dry
    @Test
    public void javaDate() throws SQLException {
        paramAssigner.assignParams(
                statement,
                params(javaDate)
        );
        ArgumentCaptor<java.sql.Date> javaDateCaptor = ArgumentCaptor.forClass(java.sql.Date.class);
        ArgumentCaptor<Integer> i1 = ArgumentCaptor.forClass(Integer.class);
        verify(statement).setDate(i1.capture(), javaDateCaptor.capture());

        assertEquals(sqlDate, javaDateCaptor.getValue());

        assertEquals(Integer.valueOf(1), i1.getValue());
    }




    private List<Object> params(Object... params) {
        return Arrays.asList(params);
    }
}
