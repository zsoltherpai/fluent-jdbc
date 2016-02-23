package org.codejargon.fluentjdbc.api.query;

import org.codejargon.fluentjdbc.api.query.inspection.DatabaseInspection;

/**
 * <p>FluentJdbc Query API to create select, update/insert, and batch update/insert queries. Immutable, thread-safe.</p>
 * @see org.codejargon.fluentjdbc.api.integration.ConnectionProvider
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

    Transaction transaction();

    /**
     * Inspection of the database (tables, columns, etc..)
     *
     * @return databaseInspection
     */
    DatabaseInspection databaseInspection();
}
