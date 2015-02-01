package org.codejargon.fluentjdbc.integration.testdata;

import java.sql.Date;
import java.time.LocalDate;

public class Dummy {
    String id;
    String string;
    LocalDate dateLocalDate;
    java.sql.Date dateSqlDate;

    public Dummy(String id, String string, LocalDate dateLocalDate, java.sql.Date dateSqlDate) {
        this.id = id;
        this.string = string;
        this.dateLocalDate = dateLocalDate;
        this.dateSqlDate = dateSqlDate;
    }

    public Dummy() {
    }

    public String id() {
        return id;
    }

    public String string() {
        return string;
    }

    public LocalDate dateLocalDate() {
        return dateLocalDate;
    }

    public Date dateSqlDate() {
        return dateSqlDate;
    }
}
