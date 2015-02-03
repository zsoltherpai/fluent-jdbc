package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.codejargon.fluentjdbc.api.query.Query;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TransactionTestService {
    private final Query query;
    private final TransactionTestService2 testService2;

    @Inject
    public TransactionTestService(Query query, TransactionTestService2 testService2) {
        this.query = query;
        this.testService2 = testService2;
    }

    public void noTransaction() {
        insert();
    }

    @Transactional
    public void transaction() {
        insert();
    }

    @Transactional
    public void transactionBreaking() {
        insert();
        throw new TransactionBreaking();
    }


    @Transactional
    public void propagatedTransaction() {
        insert();
        testService2.transactional();
    }

    @Transactional
    public void propagatedTransactionOriginalBreaking() {
        insert();
        testService2.transactional();
        throw new TransactionBreaking();
    }

    @Transactional
    public void propagatedTransactionBreaking() {
        insert();
        testService2.transactionalBreaking();
    }
    
    @Transactional(rollbackOn = {IOException.class}, ignore = {FileNotFoundException.class})
    public void rollbackRulesNoException() {
        insert();
    }

    @Transactional(rollbackOn = {IOException.class}, ignore = {FileNotFoundException.class})
    public void rollbackRulesNonRollbackException() {
        insert();
        throw new UnsupportedOperationException();
    }

    @Transactional(rollbackOn = {IOException.class}, ignore = {FileNotFoundException.class})
    public void rollbackRulesRollbackException() throws IOException {
        insert();
        throw new IOException();
    }

    @Transactional(rollbackOn = {IOException.class}, ignore = {FileNotFoundException.class})
    public void rollbackRulesIgnoredException() throws FileNotFoundException {
        insert();
        throw new FileNotFoundException();
    }

    private void insert() {
        query.update("INSERT INTO DUMMY(id) VALUES(?)").params("1").run();
    }
}
