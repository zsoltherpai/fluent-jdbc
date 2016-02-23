package org.codejargon.fluentjdbc.api.query.listen;

/**
 * This listener callback will be called after each SQL operation made through FluentJdbc.
 */
@FunctionalInterface
public interface AfterQueryListener {
    void listen(ExecutionDetails executionDetails);
}
