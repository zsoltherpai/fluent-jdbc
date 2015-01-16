package org.fluentjdbc.internal.mappers;

import org.fluentjdbc.api.mapper.ObjectMapperFactory;
import org.fluentjdbc.api.query.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObjectMapperTest {

    static final Long longColumn = 5L;
    static final Integer intColumn = 25;
    static final String stringColumn = "foo";
    static final BigDecimal bigDecimalColumn = BigDecimal.TEN;
    static final Year yearColumn = Year.of(2015);
    static final YearMonth yearMonthColumn = YearMonth.of(2015, Month.FEBRUARY);
    static final LocalDate localDateColumn = LocalDate.of(2015, Month.FEBRUARY, 15);
    static final LocalDateTime localDateTimeColumn = LocalDateTime.of(2015, Month.FEBRUARY, 15, 11, 2);
    static final Instant instantColumn = Instant.ofEpochMilli(543435L);

    @Mock
    ResultSet resultSet;
    @Mock
    ResultSetMetaData meta;

    ObjectMapperFactory factory = ObjectMapperFactory.builder().build();

    @Before
    public void setUp() throws SQLException {
        when(resultSet.getLong(1)).thenReturn(longColumn);
        when(resultSet.getInt(2)).thenReturn(intColumn);
        when(resultSet.getString(3)).thenReturn(stringColumn);
        when(resultSet.getString(4)).thenReturn(null);
        when(resultSet.getBigDecimal(5)).thenReturn(bigDecimalColumn);
        when(resultSet.getDate(6)).thenReturn(Date.valueOf(LocalDate.of(yearColumn.getValue(), Month.JANUARY, 1)));
        when(resultSet.getDate(7)).thenReturn(Date.valueOf(LocalDate.of(yearMonthColumn.getYear(), yearMonthColumn.getMonth(), 1)));
        when(resultSet.getDate(8)).thenReturn(Date.valueOf(localDateColumn));
        when(resultSet.getTimestamp(9)).thenReturn(Timestamp.valueOf(localDateTimeColumn));
        when(resultSet.getTimestamp(10)).thenReturn(Timestamp.from(instantColumn));
        when(resultSet.getTimestamp(11)).thenReturn(null);
        when(resultSet.getMetaData()).thenReturn(meta);

        mockMetaColumnNames(
                "LONG_COLUMN",
                "INT_COLUMN",
                "STRING_COLUMN",
                "STRING_NULL_COLUMN",
                "BIGDECIMAL_COLUMN",
                "YEAR_COLUMN",
                "YEARMONTH_COLUMN",
                "LOCALDATE_COLUMN",
                "LOCALDATETIME_COLUMN",
                "INSTANT_COLUMN",
                "INSTANT_NULL_COLUMN",
                "NOT_MAPPED_IN_DUMMY"
        );
    }

    @Test
    public void map() throws SQLException {
        Mapper<Dummy> mapper = factory.create(Dummy.class);
        Dummy mappedDummy = mapper.map(resultSet);
        Dummy expectedDummy = expectedDummy();
        assertThat(mappedDummy, is(equalTo(expectedDummy)));
    }

    private Dummy expectedDummy() {
        Dummy dummy = new Dummy();
        dummy.longColumn = longColumn;
        dummy.intColumn = intColumn;
        dummy.stringColumn = stringColumn;
        dummy.stringNullColumn = null;
        dummy.bigDecimalColumn = bigDecimalColumn;
        dummy.yearColumn = yearColumn;
        dummy.yearMonthColumn = yearMonthColumn;
        dummy.localDateColumn = localDateColumn;
        dummy.localDateTimeColumn = localDateTimeColumn;
        dummy.instantColumn = instantColumn;
        dummy.instantNullColumn = null;
        return dummy;
    }

    private void mockMetaColumnNames(String... columns) throws SQLException {
        when(meta.getColumnCount()).thenReturn(columns.length);
        for(int i = 1; i <= columns.length; ++i) {
            when(meta.getColumnName(i)).thenReturn(columns[i-1]);
        }
    }


    public static class Dummy {
        Long longColumn;
        Integer intColumn;
        String stringColumn;
        String stringNullColumn;
        BigDecimal bigDecimalColumn;
        Year yearColumn;
        YearMonth yearMonthColumn;
        LocalDate localDateColumn;
        LocalDateTime localDateTimeColumn;
        Instant instantColumn;
        Instant instantNullColumn;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Dummy dummy = (Dummy) o;

            if (bigDecimalColumn != null ? !bigDecimalColumn.equals(dummy.bigDecimalColumn) : dummy.bigDecimalColumn != null)
                return false;
            if (instantColumn != null ? !instantColumn.equals(dummy.instantColumn) : dummy.instantColumn != null)
                return false;
            if (instantNullColumn != null ? !instantNullColumn.equals(dummy.instantNullColumn) : dummy.instantNullColumn != null)
                return false;
            if (intColumn != null ? !intColumn.equals(dummy.intColumn) : dummy.intColumn != null) return false;
            if (localDateColumn != null ? !localDateColumn.equals(dummy.localDateColumn) : dummy.localDateColumn != null)
                return false;
            if (localDateTimeColumn != null ? !localDateTimeColumn.equals(dummy.localDateTimeColumn) : dummy.localDateTimeColumn != null)
                return false;
            if (longColumn != null ? !longColumn.equals(dummy.longColumn) : dummy.longColumn != null) return false;
            if (stringColumn != null ? !stringColumn.equals(dummy.stringColumn) : dummy.stringColumn != null)
                return false;
            if (stringNullColumn != null ? !stringNullColumn.equals(dummy.stringNullColumn) : dummy.stringNullColumn != null)
                return false;
            if (yearColumn != null ? !yearColumn.equals(dummy.yearColumn) : dummy.yearColumn != null) return false;
            if (yearMonthColumn != null ? !yearMonthColumn.equals(dummy.yearMonthColumn) : dummy.yearMonthColumn != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = longColumn != null ? longColumn.hashCode() : 0;
            result = 31 * result + (intColumn != null ? intColumn.hashCode() : 0);
            result = 31 * result + (stringColumn != null ? stringColumn.hashCode() : 0);
            result = 31 * result + (stringNullColumn != null ? stringNullColumn.hashCode() : 0);
            result = 31 * result + (bigDecimalColumn != null ? bigDecimalColumn.hashCode() : 0);
            result = 31 * result + (yearColumn != null ? yearColumn.hashCode() : 0);
            result = 31 * result + (yearMonthColumn != null ? yearMonthColumn.hashCode() : 0);
            result = 31 * result + (localDateColumn != null ? localDateColumn.hashCode() : 0);
            result = 31 * result + (localDateTimeColumn != null ? localDateTimeColumn.hashCode() : 0);
            result = 31 * result + (instantColumn != null ? instantColumn.hashCode() : 0);
            result = 31 * result + (instantNullColumn != null ? instantNullColumn.hashCode() : 0);
            return result;
        }
    }



}
