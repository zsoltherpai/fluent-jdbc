package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.integration.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)
public class TransactionTest extends TransactionTestDb {
    Injector injector;
    Query query;
    TransactionTestService testService;

    @Before
    public void setUp() {
        TransactionalConnectionProvider cp = new TransactionalConnectionProvider(ds);
        FluentJdbc fluentJdbc = new FluentJdbcBuilder().connectionProvider(cp).build();
        injector = Guice.createInjector(new FluentJdbcTransactionalModule(fluentJdbc));
        testService = injector.getInstance(TransactionTestService.class);
        query = injector.getInstance(Query.class);
    }

    @Test
    public void noTransaction() {
        testService.noTransaction();
        assertThat(count(), is(1));
    }

    @Test
    public void transaction() {
        testService.transaction();
        assertThat(count(), is(1));
    }

    @Test(expected = TransactionBreaking.class)
    public void transactionBreaking() {
        testService.transactionBreaking();
        assertThat(count(), is(0));
    }
    
    @Test
    public void propagatedTransaction() {
        testService.propagatedTransaction();
        assertThat(count(), is(2));
    }

    @Test(expected = TransactionBreaking.class)
    public void propagatedTransactionBreaking() {
        testService.propagatedTransactionBreaking();
        assertThat(count(), is(0));
    }

    @Test(expected = TransactionBreaking.class)
    public void propagatedTransactionOriginalBreaking() {
        testService.propagatedTransactionOriginalBreaking();
        assertThat(count(), is(0));
    }
    
    private int count() {
        return queryOnSentry().select("SELECT COUNT(*) FROM DUMMY").singleResult(Mappers.singleInteger());
    }
}
