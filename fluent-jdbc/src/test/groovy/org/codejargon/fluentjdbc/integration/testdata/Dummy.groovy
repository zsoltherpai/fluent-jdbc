package org.codejargon.fluentjdbc.integration.testdata

import java.time.LocalDate

class Dummy {
    String id;
    String string;
    LocalDate dateLocalDate;
    java.sql.Date dateSqlDate;
    String nullString;

    public Dummy(String id, String string, LocalDate dateLocalDate, java.sql.Date dateSqlDate) {
        this.id = id;
        this.string = string;
        this.dateLocalDate = dateLocalDate;
        this.dateSqlDate = dateSqlDate;
        this.nullString = null;
    }

    private Dummy() {
    }

    List<Object> params() {
        return [
                id,
                string,
                dateLocalDate,
                dateSqlDate,
                nullString
        ]
    }

    Map<String, Object> namedParams() {
        return [
                "id"           : id,
                "string"       : string,
                "dateLocalDate": dateLocalDate,
                "dateSqlDate"  : dateSqlDate,
                "nullString"   : nullString
        ]
    }

    public String id() {
        return id;
    }
}
