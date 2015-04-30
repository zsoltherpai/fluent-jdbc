package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.ParamSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

class ParamAssigner {
    private static final ParamSetter objectParamSetter =
            (param, statement, index) -> statement.setObject(index, param);
    
    private final Map<Class, ParamSetter> paramSetters;

    ParamAssigner(Map<Class, ParamSetter> paramSetters) {
        this.paramSetters = paramSetters;
    }

    void assignParams(PreparedStatement statement, List<Object> params) {
        int i = 1;
        for (Object param : params) {
            assignParam(statement, i, param);
            ++i;
        }
    }

    private void assignParam(PreparedStatement statement, Integer index, Object param) {
        try {
            if (param != null) {
                assignNonNull(param, statement, index);
            } else {
                assignNull(statement, index);
            }
        } catch (SQLException e) {
            throw new FluentJdbcSqlException(String.format("Error assigning parameter index %s, object %s", index, param != null ? param.getClass().getName() : "null"), e);
        }
    }

    private void assignNull(PreparedStatement statement, Integer index) throws SQLException {
        Integer sqlType;
        try {
            sqlType = statement.getParameterMetaData().getParameterType(index);
        } catch(SQLException e) {
            throw new FluentJdbcSqlException("Can't access parameter metadata, JDBC 3.0 not supported by the driver.", e);
        }
        statement.setNull(index, sqlType);
    }

    @SuppressWarnings("unchecked")
    private void assignNonNull(Object param, PreparedStatement statement, Integer index) throws SQLException {
        paramSetter(param).set(param, statement, index);
    }
    
    private ParamSetter paramSetter(Object param) {
        ParamSetter customSetter = paramSetters.get(param.getClass());
        return customSetter != null ? customSetter : objectParamSetter;
    }
}
