package org.fluentjdbc.api.query;

/**
 * Fluent Query API
 */
public interface Query {
    /**
     * Select query for the given SQL statement
     */
    SelectQuery select(String sql);

    /**
     * Update or insert query for the given SQL statement
     */
    UpdateQuery update(String sql);

    /**
     * Batch update or insert for the given SQL statement
     */
    BatchQuery batch(String sql);
}
