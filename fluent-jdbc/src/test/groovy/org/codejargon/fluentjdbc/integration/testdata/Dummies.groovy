package org.codejargon.fluentjdbc.integration.testdata;

import java.time.LocalDate;
import java.time.Month;

public class Dummies {
    static final def dummy1 = new Dummy("idValue1", "stringValue1", LocalDate.of(2014, Month.MARCH, 12), new java.sql.Date(System.currentTimeMillis()));
    static final def dummy2 = new Dummy("idValue2", "stringValue2", LocalDate.of(2014, Month.JANUARY, 2), new java.sql.Date(System.currentTimeMillis()));
}
