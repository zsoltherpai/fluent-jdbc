package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.internal.query.namedparameter.NamedTransformedSql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

class PreparedStatementFactory {
    private final QueryConfig config;

    PreparedStatementFactory(QueryConfig config) {
        this.config = config;
    }

    PreparedStatement createSingle(Connection con, SingleQuerySpecification querySpec) throws SQLException {
        SqlAndParams sqlAndParams = querySpec.sqlAndParams(config);
        PreparedStatement statement = con.prepareStatement(sqlAndParams.sql());
        singleQueryCustomization(statement, querySpec);
        assignParams(statement, sqlAndParams.params());
        return statement;
    }
    
    PreparedStatement createBatch(Connection con, NamedTransformedSql namedTransformedSql) throws SQLException {
        return con.prepareStatement(namedTransformedSql.sql());
    }

    void assignParams(PreparedStatement statement, List<Object> params) throws SQLException {
        config.paramAssigner.assignParams(statement, params);
    }

    private void singleQueryCustomization(PreparedStatement statement, SingleQuerySpecification querySpec) throws SQLException {
        if(querySpec.select.isPresent()) {
            selectCustomization(statement, querySpec);
        }
    }

    private void selectCustomization(PreparedStatement statement, SingleQuerySpecification querySpec) throws SQLException {
        selectFetchSize(statement, querySpec.select.get().fetchSize);
        maxResults(statement, querySpec.select.get().maxRows);
    }

    private void selectFetchSize(PreparedStatement statement, Optional<Integer> selectFetchSize) throws SQLException {
        Optional<Integer> activeFetchSize = config.fetchSize(selectFetchSize);
        if (activeFetchSize.isPresent()) {
            statement.setFetchSize(activeFetchSize.get());
        }
    }

    private void maxResults(PreparedStatement statement, Optional<Long> maxResults) throws SQLException {
        if(maxResults.isPresent()) {
            if(maxResults.get() > Integer.MAX_VALUE) {
                setLargeMaxRows(statement, maxResults);
            } else {
                statement.setMaxRows((int) maxResults.get().longValue());
            }
        }
    }

    private void setLargeMaxRows(PreparedStatement statement, Optional<Long> maxResults) throws SQLException {
        try {
            statement.setLargeMaxRows(maxResults.get());
        } catch(SQLException e) {
            throw new FluentJdbcException(
                    String.format(
                            "The JDBC driver %s doesn't support setLargeMaxRows(). Set max results <= Integer.MAX_VALUE",
                            statement.getConnection().getMetaData().getDriverName())
            );
        }
    }


}
