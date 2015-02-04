package org.codejargon.fluentjdbc.api.integration.guicepersist;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import org.codejargon.fluentjdbc.api.integration.guicepersist.standalone.TransactionBreaking;
import org.codejargon.fluentjdbc.api.query.Query;

public class TransactionTestService2 {
    private final Query query;

    @Inject
    public TransactionTestService2(Query query) {
        this.query = query;
    }

    @Transactional
    public void transactional() {
        insert();
    }

    @Transactional
    public void transactionalBreaking() {
        insert();
        throw new TransactionBreaking();
    }

    private void insert() {
        query.update("INSERT INTO DUMMY(id) VALUES(?)").params("2").run();
    }


}
