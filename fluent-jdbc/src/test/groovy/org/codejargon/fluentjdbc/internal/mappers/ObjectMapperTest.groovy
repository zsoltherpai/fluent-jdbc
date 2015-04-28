package org.codejargon.fluentjdbc.internal.mappers;

import org.codejargon.fluentjdbc.api.mapper.ObjectMappers
import spock.lang.Specification;

import java.sql.*;
import java.time.*;

class ObjectMapperTest extends Specification {

    static final expectedDummy = new Dummy(
            longColumn: 5L,
            intColumn: 25,
            stringColumn: "foo",
            stringNullColumn: null,
            bigDecimalColumn: BigDecimal.TEN,
            yearColumn: Year.of(2015),
            yearMonthColumn: YearMonth.of(2015, Month.FEBRUARY),
            localDateColumn: LocalDate.of(2015, Month.FEBRUARY, 15),
            localDateTimeColumn: LocalDateTime.of(2015, Month.FEBRUARY, 15, 11, 2),
            instantColumn: Instant.ofEpochMilli(543435L),
            instantNullColumn: null
    )

    ResultSet resultSet = Mock(ResultSet)
    def factory = ObjectMappers.builder().build();

    def setup() throws SQLException {
        def meta = Mock(ResultSetMetaData)

        resultSet.getLong(1) >> expectedDummy.longColumn
        meta.getColumnName(1) >> "LONG_COLUMN"
        resultSet.getInt(2) >> expectedDummy.intColumn
        meta.getColumnName(2) >> "INT_COLUMN"
        resultSet.getString(3) >> expectedDummy.stringColumn
        meta.getColumnName(3) >> "STRING_COLUMN"
        resultSet.getString(4) >> null
        meta.getColumnName(4) >> "STRING_NULL_COLUMN"
        resultSet.getBigDecimal(5) >> expectedDummy.bigDecimalColumn
        meta.getColumnName(5) >> "BIGDECIMAL_COLUMN"
        resultSet.getDate(6) >> Date.valueOf(LocalDate.of(expectedDummy.yearColumn.getValue(), Month.JANUARY, 1))
        meta.getColumnName(6) >> "YEAR_COLUMN"
        resultSet.getDate(7) >> Date.valueOf(LocalDate.of(expectedDummy.yearMonthColumn.getYear(), expectedDummy.yearMonthColumn.getMonth(), 1))
        meta.getColumnName(7) >> "YEARMONTH_COLUMN"
        resultSet.getDate(8) >> Date.valueOf(expectedDummy.localDateColumn)
        meta.getColumnName(8) >> "LOCALDATE_COLUMN"
        resultSet.getTimestamp(9) >> Timestamp.valueOf(expectedDummy.localDateTimeColumn)
        meta.getColumnName(9) >> "LOCALDATETIME_COLUMN"
        resultSet.getTimestamp(10) >> Timestamp.from(expectedDummy.instantColumn)
        meta.getColumnName(10) >> "INSTANT_COLUMN"
        resultSet.getTimestamp(11) >> null
        meta.getColumnName(11) >> "INSTANT_NULL_COLUMN"
        resultSet.getMetaData() >> meta
        meta.getColumnCount() >> 11
    }

    def map() {
        def mapper = factory.forClass(Dummy.class)
        when:
        def mappedDummy = mapper.map(resultSet)
        then:
        mappedDummy == expectedDummy
    }
}
