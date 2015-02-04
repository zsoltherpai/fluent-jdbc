package org.codejargon.fluentjdbc.api.integration.guicepersist.standalone;

import com.google.inject.persist.Transactional;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class TransactionInterceptor implements MethodInterceptor {
    private final StandaloneTxConnectionProvider standaloneTxConnectionProvider;

    TransactionInterceptor(StandaloneTxConnectionProvider standaloneTxConnectionProvider) {
        this.standaloneTxConnectionProvider = standaloneTxConnectionProvider;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Boolean newTransactionStarted = startNewTransactionIfNecessary();
        try {
            Object result = methodInvocation.proceed();
            if (newTransactionStarted) {
                standaloneTxConnectionProvider.commitActiveTransaction(Optional.empty());
            }
            return result;
        } catch (Exception e) {
            rollbackOrCommit(methodInvocation, e);
            throw e;
        } finally {
            if (newTransactionStarted) {
                standaloneTxConnectionProvider.removeActiveTransactionConnection();
            }
        }
    }

    private Boolean startNewTransactionIfNecessary() {
        if (!standaloneTxConnectionProvider.hasActiveTransaction()) {
            standaloneTxConnectionProvider.startNewTransaction();
            return true;
        } else {
            return false;
        }
    }

    private void rollbackOrCommit(MethodInvocation methodInvocation, Exception e) {
        if (rollbackNecessary(e, transactional(methodInvocation))) {
            standaloneTxConnectionProvider.rollbackActiveTransaction();
        } else {
            standaloneTxConnectionProvider.commitActiveTransaction(Optional.of(e));
        }
    }



    private Transactional transactional(MethodInvocation methodInvocation) {
        Method method = methodInvocation.getMethod();
        Class<?> targetClass = methodInvocation.getThis().getClass();
        Transactional transactional = method.getAnnotation(Transactional.class);
        if (null == transactional) {
            transactional = targetClass.getAnnotation(Transactional.class);
        }
        if (null == transactional) {
            transactional = DefaultTransactionalDummy.class.getAnnotation(Transactional.class);
        }
        return transactional;
    }

    private boolean rollbackNecessary(Exception cause, Transactional transactional) {
        return !has(transactional.rollbackOn(), cause).isEmpty() && has(transactional.ignore(), cause).isEmpty();
    }

    private List<Class<? extends Exception>> has(Class<? extends Exception>[] exceptions, Exception cause) {
        return Arrays.asList(exceptions).stream().filter(e -> e.isInstance(cause)).collect(Collectors.toList());
    }

    @Transactional
    private static class DefaultTransactionalDummy {
        private DefaultTransactionalDummy() {
        }
    }
}

