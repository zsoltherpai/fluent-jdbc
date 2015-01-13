package org.fluentjdbc.api.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T map(ResultSet rs) throws SQLException;
}
