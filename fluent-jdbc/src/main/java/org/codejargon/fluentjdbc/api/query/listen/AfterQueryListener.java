package org.codejargon.fluentjdbc.api.query.listen;

@FunctionalInterface
public interface AfterQueryListener {
    void listen(ExecutionDetails executionDetails);
}
