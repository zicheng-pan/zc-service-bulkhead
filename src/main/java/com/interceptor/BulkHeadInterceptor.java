package com.interceptor;

import com.annotation.Asynchronous;
import com.annotation.Bulkhead;
import com.annotation.interceptor.AroundInvoke;
import com.interceptorAPI.InvocationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class BulkHeadInterceptor {

    private final Map<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<Bulkhead, ExecutorService>();
    private final Map<Bulkhead, Semaphore> semaphoresCache = new ConcurrentHashMap<Bulkhead, Semaphore>();

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Bulkhead bulkhead = findBulkHeadAnnotation(method);
        if (bulkhead == null) {
            return context.proceed();
        }
        System.out.println("this is bulkhead interceptor");
//        System.out.println(bulkhead);
        if (isThreadIsolation(method)) {
            // 使用线程池
            ExecutorService executorService = executorsCache.computeIfAbsent(bulkhead, key -> {
                        int fixedSize = bulkhead.value();
                        int waitingTaskQueue = bulkhead.waitingTaskQueue();
                        ThreadPoolExecutor executor = new ThreadPoolExecutor(fixedSize, fixedSize,
                                0, TimeUnit.MILLISECONDS,
                                new ArrayBlockingQueue<>(waitingTaskQueue)
                        );
                        return executor;
                    }
            );
            Future<Object> future = executorService.submit(context::proceed);
            return future.get();
        } else {
            // 使用信号量
            Semaphore semaphore = semaphoresCache.computeIfAbsent(bulkhead, key -> {
                int maxConcurrentRequests = bulkhead.value();
                return new Semaphore(maxConcurrentRequests);
            });
            Object result = null;
            try {
                semaphore.acquire();
                result = context.proceed();
            } finally {
                semaphore.release();
            }

            return result;
        }
    }

    private Bulkhead findBulkHeadAnnotation(Method method) {
        //TODO 学习一下封装好的代码
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (Objects.equals(annotation.annotationType(), Bulkhead.class)) {
                return (Bulkhead) annotation;
            }
        }
        return method.getDeclaringClass().getAnnotation(Bulkhead.class);
    }

    private boolean isThreadIsolation(Method method) {
        //通过 Asynchronous 这个注解判断是不是 线程池隔离
        return method.isAnnotationPresent(Asynchronous.class);
    }
}
