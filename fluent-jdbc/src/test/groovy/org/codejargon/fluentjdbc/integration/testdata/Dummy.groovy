package org.codejargon.fluentjdbc.integration.testdata

import java.time.LocalDate

class Dummy {
    String id
    String string
    LocalDate dateLocalDate
    java.sql.Date dateSqlDate
    String nullString
    byte[] bytearray

    Dummy(String id, String string, LocalDate dateLocalDate, java.sql.Date dateSqlDate, byte[] bytearray) {
        this.id = id
        this.string = string
        this.dateLocalDate = dateLocalDate
        this.dateSqlDate = dateSqlDate
        this.nullString = null
        this.bytearray = bytearray
    }

    private Dummy() {
    }

    List<Object> params() {
        return [
                id,
                string,
                dateLocalDate,
                dateSqlDate,
                nullString,
                bytearray
        ]
    }

    Map<String, Object> namedParams() {
        return [
                "id"           : id,
                "string"       : string,
                "dateLocalDate": dateLocalDate,
                "dateSqlDate"  : dateSqlDate,
                "nullString"   : nullString,
                "bytearray"    : bytearray
        ]
    }

     String id() {
        return id;
    }
}
