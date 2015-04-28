package org.codejargon.fluentjdbc.api.integration.guicepersist;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional
import org.codejargon.fluentjdbc.api.query.Query;

public class TransactionTestService2 {
    private final Query query

    @Inject
    TransactionTestService2(Query query) {
        this.query = query
    }

    @Transactional
    def transactional() {
        insert()
    }

    @Transactional
    def transactionalBreaking() {
        insert()
        throw new TransactionBreaking()
    }

    def insert() {
        query.update("INSERT INTO DUMMY(id) VALUES(?)").params("2").run()
    }


}
