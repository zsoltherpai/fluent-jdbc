package org.codejargon.fluentjdbc.api.query;

public interface SqlErrorHandling<T> {
    T errorHandler();
}
