package org.codejargon.fluentjdbc.internal.mappers

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth

class Dummy {
    Long longColumn
    Integer intColumn
    String stringColumn
    String stringNullColumn
    BigDecimal bigDecimalColumn
    Year yearColumn
    YearMonth yearMonthColumn
    LocalDate localDateColumn
    LocalDateTime localDateTimeColumn
    Instant instantColumn
    Instant instantNullColumn

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Dummy dummy = (Dummy) o

        if (bigDecimalColumn != dummy.bigDecimalColumn) return false
        if (instantColumn != dummy.instantColumn) return false
        if (instantNullColumn != dummy.instantNullColumn) return false
        if (intColumn != dummy.intColumn) return false
        if (localDateColumn != dummy.localDateColumn) return false
        if (localDateTimeColumn != dummy.localDateTimeColumn) return false
        if (longColumn != dummy.longColumn) return false
        if (stringColumn != dummy.stringColumn) return false
        if (stringNullColumn != dummy.stringNullColumn) return false
        if (yearColumn != dummy.yearColumn) return false
        if (yearMonthColumn != dummy.yearMonthColumn) return false

        return true
    }

    int hashCode() {
        int result
        result = longColumn.hashCode()
        result = 31 * result + intColumn.hashCode()
        result = 31 * result + stringColumn.hashCode()
        result = 31 * result + (stringNullColumn != null ? stringNullColumn.hashCode() : 0)
        result = 31 * result + bigDecimalColumn.hashCode()
        result = 31 * result + yearColumn.hashCode()
        result = 31 * result + yearMonthColumn.hashCode()
        result = 31 * result + localDateColumn.hashCode()
        result = 31 * result + localDateTimeColumn.hashCode()
        result = 31 * result + instantColumn.hashCode()
        result = 31 * result + (instantNullColumn != null ? instantNullColumn.hashCode() : 0)
        return result
    }
}
