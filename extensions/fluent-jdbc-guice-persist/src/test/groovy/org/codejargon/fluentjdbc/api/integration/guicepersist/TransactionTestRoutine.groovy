package org.codejargon.fluentjdbc.api.integration.guicepersist;

import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.FluentJdbcException
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.junit.Before
import spock.lang.Specification;

import javax.inject.Inject

abstract class TransactionTestRoutine extends Specification {
    protected abstract Injector injector()

    @Inject
    TransactionTestService testService
    @Inject
    Query query

    @Before
    def injectAndTableCreate() {
        injector().injectMembers(this);
        try {
            query.update("DROP TABLE DUMMY").run();
        } catch (FluentJdbcException e) {
            // ignore
        }
        query.update("CREATE TABLE DUMMY (id VARCHAR(255) PRIMARY KEY)").run();
    }

    def "No transaction"() {
        when:
        testService.noTransaction()
        then:
        countIs(1)
    }


    def "Transaction"() {
        when:
        testService.transaction()
        then:
        countIs(1)
    }

    def "Transaction breaking"() {
        when:
        try {
            testService.transactionBreaking()
        } catch (TransactionBreaking e) {
            // ignore
        }
        then:
        countIs(0)
    }

    def "Propagated transaction"() {
        when:
        testService.propagatedTransaction()
        then:
        countIs(2)
    }

    def propagatedTransactionBreaking() {
        when:
        try {
            testService.propagatedTransactionBreaking()
        } catch (TransactionBreaking e) {
            // ignore
        }
        then:
        countIs(0)
    }

    def propagatedTransactionOriginalBreaking() {
        when:
        try {
            testService.propagatedTransactionOriginalBreaking()
        } catch (TransactionBreaking e) {
            // ignore
        }
        then:
        countIs(0)
    }

    def rollbackRulesNoException() {
        when:
        testService.rollbackRulesNoException()
        then:
        countIs(1)
    }

    def rollbackRulesNonRollbackException() {
        when:
        try {
            testService.rollbackRulesNonRollbackException()
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        then:
        countIs(1)

    }

    def rollbackRulesRollbackException() {
        when:
        try {
            testService.rollbackRulesRollbackException()
        } catch (IOException e) {
            // ignore
        }
        then:
        countIs(0);
    }

    def rollbackRulesIgnoredException() {
        when:
        try {
            testService.rollbackRulesIgnoredException()
        } catch (FileNotFoundException e) {
            // ignore
        }
        then:
        countIs(1);
    }

    void countIs(int i) {
        assert inserted() == i
    }

    def inserted() {
        return query.select("SELECT COUNT(*) FROM DUMMY").singleResult(Mappers.singleInteger())
    }
}
