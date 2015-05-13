package org.codejargon.fluentjdbc.internal.query

import org.codejargon.fluentjdbc.api.FluentJdbcBuilder
import org.codejargon.fluentjdbc.api.query.Query;
import spock.lang.Specification;

import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet;

class DatabaseInspectionTest extends Specification {

    def connection = Mock(Connection)
    def metaData = Mock(DatabaseMetaData)
    def resultset = Mock(ResultSet)

    Query query

    def setup() {
        connection.getMetaData() >> metaData
        query = new FluentJdbcBuilder().connectionProvider(
                { q -> q.receive(connection)}
        ).build().query();
    }

    def "Access to metaData"() {
        def expectedVersion = 3
        given:
        metaData.getJDBCMajorVersion() >> expectedVersion
        when:
        int resultVersion = query.databaseInspection().accessMetaData(
                {meta -> return meta.getJDBCMajorVersion()}
        );
        then:
        resultVersion == expectedVersion
    }

    def "Select from metaData"() {
        given:
        def rsIndex = 1
        def tableNames = ["1", "2"]
        resultset.next() >> true >> true >> false
        resultset.getString(rsIndex) >> tableNames.get(0) >> tableNames.get(1)
        metaData.getTables(null, null, null, null) >> resultset
        when:
        def results = query.databaseInspection()
                .selectFromMetaData({meta -> return meta.getTables(null, null, null, null)})
                .listResult({rs -> rs.getString(rsIndex)})
        then:
        results.size() == tableNames.size()
        results.get(0) == tableNames.get(0)
        results.get(1) == tableNames.get(1)
        1 * resultset.close()
    }


}
