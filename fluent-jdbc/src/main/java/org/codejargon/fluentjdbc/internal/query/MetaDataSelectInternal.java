package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.query.Mapper;
import org.codejargon.fluentjdbc.api.query.inspection.MetaDataResultSet;
import org.codejargon.fluentjdbc.api.query.inspection.MetaDataSelect;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MetaDataSelectInternal implements MetaDataSelect {
    private final QueryInternal query;
    private final MetaDataResultSet select;

    public MetaDataSelectInternal(QueryInternal query, MetaDataResultSet select) {
        this.query = query;
        this.select = select;
    }

    @Override
    public <T> List<T> listResult(Mapper<T> mapper) {
        return query.query(
                connection -> {
                    List<T> results = new ArrayList<>();
                    try(ResultSet rs = select.select(connection.getMetaData())) {
                        while(rs.next()) {
                            results.add(mapper.map(rs));
                        }
                    }
                    return results;
                },
                "JDBC Select from MetaData"
        );
    }
}
