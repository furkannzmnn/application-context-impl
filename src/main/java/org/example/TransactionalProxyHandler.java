package org.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TransactionalProxyHandler implements InvocationHandler {
    private final Object objectToHandle;

    public TransactionalProxyHandler(Object objectToHandle) {
        this.objectToHandle = objectToHandle;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isCanTransactional(method)) {
            System.out.println("TRANSACTIONAL CALL STARTED HANDLER SERVICE " + method.getName());
            final Object result = method.invoke(objectToHandle, args);
            System.out.println("TRANSACTIONAL CALL END HANDLER SERVICE " + method.getName());
            return result;
        }
        return method.invoke(proxy, args);
    }

    private boolean isCanTransactional(Method method) {
        try {
            return objectToHandle.getClass().getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Transactional.class);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
