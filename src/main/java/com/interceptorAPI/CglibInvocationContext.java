package com.interceptorAPI;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CglibInvocationContext implements InvocationContext {

    private final Object source;
    private final Method method;
    private Object[] args;
    private final MethodProxy methodProxy;

    private final Map<String, Object> contextData;

    public CglibInvocationContext(Object source, Method method, MethodProxy methodProxy, Object... args) {
        this.source = source;
        this.method = method;
        this.args = args;
        this.methodProxy = methodProxy;
        this.contextData = new HashMap<>();
    }


    @Override
    public Object getTarget() {
        return source;
    }

    @Override
    public Object getTimer() {
        throw new UnsupportedOperationException("ReflectiveMethodInvocationContext does not support to get the Constructor!");
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Constructor<?> getConstructor() {
        throw new UnsupportedOperationException("ReflectiveMethodInvocationContext does not support to get the Constructor!");
    }

    @Override
    public Object[] getParameters() {
        return args;
    }

    @Override
    public void setParameters(Object[] params) {
        this.args = params;
    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object proceed() throws Exception {
        try {
            return methodProxy.invokeSuper(getTarget(), getParameters());
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }
    }
}
