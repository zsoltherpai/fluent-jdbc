package org.codejargon.fluentjdbc.integration.testdata;

public class TestQuery {
    public static final String insertSqlPositional = "INSERT INTO DUMMY(id, string, dateLocalDate, dateSqlDate) VALUES(?, ?, ?, ?)";
    public static final String insertSqlNamed = "INSERT INTO DUMMY(id, string, dateLocalDate, dateSqlDate) VALUES(:id, :string, :dateLocalDate, :dateSqlDate)";
    public static final String selectAllSql = "SELECT * FROM DUMMY";
    public static final String createDummyTable = "CREATE TABLE DUMMY (id VARCHAR(255) PRIMARY KEY, string VARCHAR(255), dateLocalDate DATE, dateSqlDate DATE)";
}
