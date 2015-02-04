package org.codejargon.fluentjdbc.api.integration.guicepersist;

import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.integration.guicepersist.standalone.TransactionBreaking;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class TransactionTestRoutine {
    protected abstract Injector injector();

    @Inject
    private TransactionTestService testService;
    @Inject
    private Query query;

    @Before
    public void injectAndTableCreate() {
        injector().injectMembers(this);
        try {
            query.update("DROP TABLE DUMMY").run();
        } catch(FluentJdbcException e) {
            // ignore
        }
        query.update("CREATE TABLE DUMMY (id VARCHAR(255) PRIMARY KEY)").run();
    }

    @Test
    public void noTransaction() {
        testService.noTransaction();
        assertCount(1);
    }

    @Test
    public void transaction() {
        testService.transaction();
        assertCount(1);
    }

    @Test
    public void transactionBreaking() {
        try {
            testService.transactionBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertCount(0);
    }

    @Test
    public void propagatedTransaction() {
        testService.propagatedTransaction();
        assertCount(2);
    }

    @Test
    public void propagatedTransactionBreaking() {
        try {
            testService.propagatedTransactionBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertCount(0);
    }

    @Test
    public void propagatedTransactionOriginalBreaking() {
        try {
            testService.propagatedTransactionOriginalBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertCount(0);
    }

    @Test
    public void rollbackRulesNoException() {
        testService.rollbackRulesNoException();
        assertCount(1);
    }

    @Test
    public void rollbackRulesNonRollbackException() {
        try {
            testService.rollbackRulesNonRollbackException();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertCount(1);

    }

    @Test
    public void rollbackRulesRollbackException() throws IOException {
        try {
            testService.rollbackRulesRollbackException();
        } catch (IOException e) {
            // ignore
        }
        assertThat(inserted(), is(0));
    }

    @Test
    public void rollbackRulesIgnoredException() throws FileNotFoundException {
        try {
            testService.rollbackRulesIgnoredException();
        } catch (FileNotFoundException e) {
            // ignore
        }
        assertCount(1);
    }

    private void assertCount(int i) {
        assertThat(inserted(), is(i));
    }

    private int inserted() {
        return query.select("SELECT COUNT(*) FROM DUMMY").singleResult(Mappers.singleInteger());
    }
}
