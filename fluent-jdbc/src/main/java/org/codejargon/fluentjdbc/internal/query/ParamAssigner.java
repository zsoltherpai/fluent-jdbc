package org.codejargon.fluentjdbc.internal.query;

import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;
import org.codejargon.fluentjdbc.api.ParamSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ParamAssigner {
    private volatile boolean trySqlTypeForNull = true;

    private static final ParamSetter fallbackParamSetter =
            (param, statement, index) -> {
                if (!param.getClass().isEnum()) {
                    statement.setObject(index, param);
                } else {
                    statement.setString(index, param.toString());
                }
            };

    private final Map<Class, ParamSetter> paramSetters;

    ParamAssigner(Map<Class, ParamSetter> paramSetters) {
        this.paramSetters = paramSetters;
    }

    void assignParams(PreparedStatement statement, List<?> params) {
        int i = 1;
        for (Object param : params) {
            assignParam(
                    statement,
                    i,
                    param instanceof Optional ? ((Optional<?>) param).orElse(null) : param
            );
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
        Optional<Integer> sqlType = sqlTypeForNull(statement, index);
        if(sqlType.isPresent()) {
            statement.setNull(index, sqlType.get());
        } else {
            statement.setObject(index, null);
        }
    }

    private Optional<Integer> sqlTypeForNull(PreparedStatement statement, Integer index) {
        if(trySqlTypeForNull) {
            try {
                return Optional.of(statement.getParameterMetaData().getParameterType(index));
            } catch (SQLException e) {
                trySqlTypeForNull = false;
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private void assignNonNull(Object param, PreparedStatement statement, Integer index) throws SQLException {
        paramSetter(param).set(param, statement, index);
    }

    private ParamSetter paramSetter(Object param) {
        ParamSetter customSetter = paramSetters.get(param.getClass());
        return customSetter != null ? customSetter : fallbackParamSetter;
    }


}
