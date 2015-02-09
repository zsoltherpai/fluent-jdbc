package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.persist.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.codejargon.fluentjdbc.internal.support.Lists;
import org.codejargon.fluentjdbc.internal.support.Select;

class TransactionInterceptor implements MethodInterceptor {
    private final StandaloneTxConnectionProvider standaloneTxConnectionProvider;

    TransactionInterceptor(StandaloneTxConnectionProvider standaloneTxConnectionProvider) {
        this.standaloneTxConnectionProvider = standaloneTxConnectionProvider;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Boolean newTransactionStarted = startNewTransactionIfNecessary();
        try {
            return invokeMethodAndCommitIfNecessary(methodInvocation, newTransactionStarted);
        } catch (Exception e) {
            rollbackOrCommit(methodInvocation, e);
            throw e;
        } finally {
            if (newTransactionStarted) {
                standaloneTxConnectionProvider.removeActiveTransactionConnection();
            }
        }
    }

    private Object invokeMethodAndCommitIfNecessary(MethodInvocation methodInvocation, Boolean newTransactionStarted) throws Throwable {
        Object result = methodInvocation.proceed();
        if (newTransactionStarted) {
            standaloneTxConnectionProvider.commitActiveTransaction();
        }
        return result;
    }

    private Boolean startNewTransactionIfNecessary() {
        final Boolean newTransactionStarted;
        if (!standaloneTxConnectionProvider.hasActiveTransaction()) {
            standaloneTxConnectionProvider.startNewTransaction();
            newTransactionStarted = true;
        } else {
            newTransactionStarted = false;
        }
        return newTransactionStarted;
    }

    private void rollbackOrCommit(MethodInvocation methodInvocation, Exception e) {
        if (rollbackNecessary(e, transactional(methodInvocation))) {
            standaloneTxConnectionProvider.rollbackActiveTransaction();
        } else {
            standaloneTxConnectionProvider.commitActiveTransaction();
        }
    }

    private Transactional transactional(MethodInvocation methodInvocation) {
        return Select.firstNonNull(
                () -> methodInvocation.getMethod().getAnnotation(Transactional.class),
                () -> methodInvocation.getThis().getClass().getAnnotation(Transactional.class),
                this::defaultTransactional
        );
    }
    
    private boolean rollbackNecessary(Exception cause, Transactional transactional) {
        return has(transactional.rollbackOn(), cause) && !has(transactional.ignore(), cause);
    }

    private Boolean has(Class<? extends Exception>[] exceptions, Exception cause) {
        return Lists.copyOf(exceptions).stream().filter(e -> e.isInstance(cause)).findAny().isPresent();
    }

    private Transactional defaultTransactional() {
        return DefaultTransactionalDummy.class.getAnnotation(Transactional.class);
    }
    
    @Transactional
    private static class DefaultTransactionalDummy {
        private DefaultTransactionalDummy() {
        }
    }
}

