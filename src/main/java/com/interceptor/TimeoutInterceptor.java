package com.interceptor;

import com.annotation.Timeout;
import com.annotation.interceptor.AroundInvoke;
import com.interceptorAPI.InvocationContext;
import com.utlis.TimeUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class TimeoutInterceptor {

    // TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = newCachedThreadPool();

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Timeout timeout = findTimeoutAnnotation(method);
        if (timeout != null) {
            System.out.println("this is timeout interceptor");
            Future<Object> future = executor.submit(context::proceed);

            try {
                return future.get(timeout.value(), TimeUtils.toTimeUnit(timeout.unit()));
            } catch (TimeoutException e) {
                // 这里表示方法调用已经超时，可以进行捕获处理
                future.cancel(true);
            }
        }
        return context.proceed();
    }

    private Timeout findTimeoutAnnotation(Method method) {
        Timeout timeout = method.getAnnotation(Timeout.class);
        if (timeout == null) {
            timeout = method.getDeclaringClass().getAnnotation(Timeout.class);
        }
        return timeout;
    }
}
