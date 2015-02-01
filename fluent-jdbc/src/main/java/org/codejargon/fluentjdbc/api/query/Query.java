package org.codejargon.fluentjdbc.api.query;

/**
 * Fluent Query API. Thread-safe.
 */
public interface Query {
    /**
     * Creates a select query for a SQL statement
     *
     * @param sql SQL statement
     * @return Select query for the SQL statement
     */
    SelectQuery select(String sql);

    /**
     * Creates an update or insert query for a SQL statement
     *
     * @param sql SQL statement
     * @return Update query for the SQL statement
     */
    UpdateQuery update(String sql);

    /**
     * Creates a batch update or insert query for a SQL statement
     *
     * @param sql SQL statement
     * @return Batch update or insert query for the SQL statement
     */
    BatchQuery batch(String sql);
}
