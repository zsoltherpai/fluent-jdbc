package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.codejargon.fluentjdbc.api.query.Query;

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

    private void insert() {
        query.update("INSERT INTO DUMMY(id) VALUES(?)").params("1").run();
    }
}
