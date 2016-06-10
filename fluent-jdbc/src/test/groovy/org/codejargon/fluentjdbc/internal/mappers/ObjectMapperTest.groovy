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
            instantNullColumn: null,
            optionalNonEmptyColumn: Optional.of("nonEmpty"),
            optionalEmptyColumn: Optional.empty()

    )

    ResultSet resultSet = Mock(ResultSet)
    def factory = ObjectMappers.builder().build();

    def setup() throws SQLException {
        def meta = Mock(ResultSetMetaData)

        resultSet.getLong(1) >> expectedDummy.longColumn
        meta.getColumnLabel(1) >> "LONG_COLUMN"
        resultSet.getInt(2) >> expectedDummy.intColumn
        meta.getColumnLabel(2) >> "INT_COLUMN"
        resultSet.getString(3) >> expectedDummy.stringColumn
        meta.getColumnLabel(3) >> "STRING_COLUMN"
        resultSet.getString(4) >> null
        meta.getColumnLabel(4) >> "STRING_NULL_COLUMN"
        resultSet.getBigDecimal(5) >> expectedDummy.bigDecimalColumn
        meta.getColumnLabel(5) >> "BIGDECIMAL_COLUMN"
        resultSet.getDate(6) >> Date.valueOf(LocalDate.of(expectedDummy.yearColumn.getValue(), Month.JANUARY, 1))
        meta.getColumnLabel(6) >> "YEAR_COLUMN"
        resultSet.getDate(7) >> Date.valueOf(LocalDate.of(expectedDummy.yearMonthColumn.getYear(), expectedDummy.yearMonthColumn.getMonth(), 1))
        meta.getColumnLabel(7) >> "YEARMONTH_COLUMN"
        resultSet.getDate(8) >> Date.valueOf(expectedDummy.localDateColumn)
        meta.getColumnLabel(8) >> "LOCALDATE_COLUMN"
        resultSet.getTimestamp(9) >> Timestamp.valueOf(expectedDummy.localDateTimeColumn)
        meta.getColumnLabel(9) >> "LOCALDATETIME_COLUMN"
        resultSet.getTimestamp(10) >> Timestamp.from(expectedDummy.instantColumn)
        meta.getColumnLabel(10) >> "INSTANT_COLUMN"
        resultSet.getTimestamp(11) >> null
        meta.getColumnLabel(11) >> "INSTANT_NULL_COLUMN"
        resultSet.getString(12) >> expectedDummy.optionalNonEmptyColumn.get()
        meta.getColumnLabel(12) >> "OPTIONAL_NON_EMPTY_COLUMN"
        resultSet.getTimestamp(13) >> null
        meta.getColumnLabel(13) >> "OPTIONAL_EMPTY_COLUMN"
        resultSet.getMetaData() >> meta
        meta.getColumnCount() >> 13
    }

    def map() {
        def mapper = factory.forClass(Dummy.class)
        when:
        def mappedDummy = mapper.map(resultSet)
        then:
        mappedDummy == expectedDummy
    }
}
