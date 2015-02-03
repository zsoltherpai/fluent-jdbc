package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)
public class TransactionTest extends TransactionTestDb {
    static Injector injector;
    static Query query;
    static TransactionTestService testService;

    @BeforeClass
    public static void initFluentJdbcAndTestService() {
        TransactionalConnectionProvider cp = new TransactionalConnectionProvider(ds);
        FluentJdbc fluentJdbc = new FluentJdbcBuilder().connectionProvider(cp).build();
        injector = Guice.createInjector(new FluentJdbcTransactionalModule(fluentJdbc));
        testService = injector.getInstance(TransactionTestService.class);
        query = injector.getInstance(Query.class);
    }

    @Test
    public void noTransaction() {
        testService.noTransaction();
        assertThat(inserted(), is(1));
    }

    @Test
    public void transaction() {
        testService.transaction();
        assertThat(inserted(), is(1));
    }

    @Test
    public void transactionBreaking() {
        try {
            testService.transactionBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertThat(inserted(), is(0));
    }

    @Test
    public void propagatedTransaction() {
        testService.propagatedTransaction();
        assertThat(inserted(), is(2));
    }

    @Test
    public void propagatedTransactionBreaking() {
        try {
            testService.propagatedTransactionBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertThat(inserted(), is(0));
    }

    @Test
    public void propagatedTransactionOriginalBreaking() {
        try {
            testService.propagatedTransactionOriginalBreaking();
        } catch (TransactionBreaking e) {
            // ignore
        }
        assertThat(inserted(), is(0));
    }

    @Test
    public void rollbackRulesNoException() {
        testService.rollbackRulesNoException();
        assertThat(inserted(), is(1));
    }

    @Test
    public void rollbackRulesNonRollbackException() {
        try {
            testService.rollbackRulesNonRollbackException();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertThat(inserted(), is(1));

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
        } catch(FileNotFoundException e) {
            // ignore
        }
        assertThat(inserted(), is(1));
    }

    private int inserted() {
        return queryOnSentry().select("SELECT COUNT(*) FROM DUMMY").singleResult(Mappers.singleInteger());
    }
}
