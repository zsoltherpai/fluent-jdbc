package org.codejargon.fluentjdbc.api.query;

import java.sql.SQLException;
import java.util.Optional;

public interface SqlErrorHandler {
    enum Action {
        RETRY
    }

    /**
     * Handles SQL errors, may implement logging, etc. The handler should always rethrow an exception in case of
     * a critical error. Otherwise ignore / retry action.
     *
     * @param e the error
     * @param sql The sql query. Always present unless the error was thrown by direct plainConnection() usage.
     * @return In case no exception is thrown, otherwise action needs to be returned ( eg retry ).
     */
    Action handle(SQLException e, Optional<String> sql) throws SQLException;
}
