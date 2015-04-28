package org.codejargon.fluentjdbc.api.integration.guicepersist;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional
import org.codejargon.fluentjdbc.api.query.Query;

class TransactionTestService {
    final Query query
    final TransactionTestService2 testService2

    @Inject
    TransactionTestService(Query query, TransactionTestService2 testService2) {
        this.query = query
        this.testService2 = testService2
    }

    def noTransaction() {
        insert()
    }

    @Transactional
    def transaction() {
        insert()
    }

    @Transactional
    def transactionBreaking() {
        insert()
        throw new TransactionBreaking()
    }


    @Transactional
    def propagatedTransaction() {
        insert()
        testService2.transactional()
    }

    @Transactional
    def propagatedTransactionOriginalBreaking() {
        insert()
        testService2.transactional()
        throw new TransactionBreaking()
    }

    @Transactional
    def propagatedTransactionBreaking() {
        insert()
        testService2.transactionalBreaking()
    }
    
    @Transactional(rollbackOn = IOException.class, ignore = FileNotFoundException.class)
    def rollbackRulesNoException() {
        insert()
    }

    @Transactional(rollbackOn = IOException.class, ignore = FileNotFoundException.class)
    def rollbackRulesNonRollbackException() {
        insert()
        throw new UnsupportedOperationException()
    }

    @Transactional(rollbackOn = IOException.class, ignore = FileNotFoundException.class)
    def rollbackRulesRollbackException() {
        insert()
        throw new IOException()
    }

    @Transactional(rollbackOn = IOException.class, ignore = FileNotFoundException.class)
    def rollbackRulesIgnoredException() {
        insert()
        throw new FileNotFoundException()
    }

    def insert() {
        query.update("INSERT INTO DUMMY(id) VALUES(?)").params("1").run()
    }
}
