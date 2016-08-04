package org.codejargon.fluentjdbc.api.query;

/**
 * Results of an update / insert
 */
public interface UpdateResult {
    long affectedRows();
}
