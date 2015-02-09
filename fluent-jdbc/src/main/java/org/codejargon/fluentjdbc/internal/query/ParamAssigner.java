package org.codejargon.fluentjdbc.internal.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codejargon.fluentjdbc.api.ParamSetter;
import org.codejargon.fluentjdbc.internal.support.Sets;
import org.codejargon.fluentjdbc.api.FluentJdbcSqlException;

public class ParamAssigner {
    private final Map<Class, ParamSetter> paramSetters;
    private Set<String> paramTypeLookupFailsOnDriver = Sets.empty();

    public ParamAssigner(Map<Class, ParamSetter> paramSetters) {
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
            if (param == null) {
                assignNull(statement, index);
            } else {
                assignNonNull(param, statement, index);
            }
        } catch (SQLException e) {
            throw new FluentJdbcSqlException(String.format("Error mapping index %s, object %s", index, param != null ? param.getClass().getName() : "null"), e);
        }
    }

    private void assignNull(PreparedStatement statement, Integer index) throws SQLException {
        if(!paramTypeLookupFailsOnDriver.contains(driverName(statement))) {
            assignNullWithSqlType(statement, index);
        } else {
            assignNullWithoutSqlType(statement, index);
        }
    }



    @SuppressWarnings("unchecked")
    private void assignNonNull(Object param, PreparedStatement statement, Integer index) throws SQLException {
        if (paramSetters.containsKey(param.getClass())) {
            paramSetters.get(param.getClass()).set(param, statement, index);
        } else {
            statement.setObject(index, param);
        }
    }

    private void assignNullWithSqlType(PreparedStatement statement, Integer index) throws SQLException {
        try {
            Integer sqlType = statement.getParameterMetaData().getParameterType(index);
            statement.setNull(index, sqlType);
        } catch(SQLException e) {
            // Parametertype lookup not supported by the driver, fallback.
            paramTypeLookupFailsOnDriver = Sets.merge(paramTypeLookupFailsOnDriver, Sets.immutableOf(new String[] {driverName(statement)}));
            assignNullWithoutSqlType(statement, index);
        }
    }

    private static final Set<String> nullWithObject = Sets.immutableOf(
            new String[] { "Microsoft SQL Server", "Informix"}
    );
    private static final Set<String> nullWithVarchar = Sets.immutableOf(
            new String[] { "DB2", "jConnect", "SQLServer", "Apache Derby" }
    );
    
    private void assignNullWithoutSqlType(PreparedStatement statement, Integer index) throws FluentJdbcSqlException {
        
        try {
            String driverName = driverName(statement);
            if(hasPrefix(nullWithObject, driverName)) {
                statement.setObject(index, null);
            } else if(hasPrefix(nullWithVarchar, driverName)) {
                statement.setNull(index, Types.VARCHAR);
            } else {
                statement.setNull(index, Types.NULL);
            }
        } catch(SQLException ex) {
            throw new FluentJdbcSqlException(String.format("Failed to assign null value at index %s", index), ex);
        }
    }

    private String driverName(PreparedStatement statement) throws SQLException {
        return statement.getConnection().getMetaData().getDriverName();
    }

    private boolean hasPrefix(Set<String> driverPrefixes, String driver) {
        for(String string : driverPrefixes) {
            if(driver.startsWith(string)) {
                return true;
            }
        }
        return false;
    }
}
