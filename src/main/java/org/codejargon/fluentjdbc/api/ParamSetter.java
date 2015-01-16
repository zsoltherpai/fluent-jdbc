package org.codejargon.fluentjdbc.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * API for setting parameters of custom types (eg. Joda time, etc..)
 *
 * Note that support for java.time is implemented by Fluent-Jdbc by default
 */
public interface ParamSetter<T> {
    void set(T param, PreparedStatement statement, Integer index) throws SQLException;
}
