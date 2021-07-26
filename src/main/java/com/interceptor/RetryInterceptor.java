package com.interceptor;

import com.annotation.Retry;
import com.annotation.interceptor.AroundInvoke;
import com.interceptorAPI.InvocationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class RetryInterceptor {


    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Retry retry = (Retry) findAnnotation(method, Retry.class);

        if (retry != null) {

            long delay = retry.delay();
            long jitter = retry.jitter();
            int max_retry = retry.maxRetries();
            long max_duration = retry.maxDuration();
            Class<? extends Throwable>[] aborts = retry.abortOn();
            Class<? extends Throwable>[] retrys = retry.retryOn();

            // 首先调用一次
            Object result = null;
            try {
                result = context.proceed();
            } catch (Throwable e) {
                if (isAbortOn(retry, e.getCause().getCause())) {
                    return e.getCause().getCause().getMessage();
                } else if (!isRetryOn(retry, e.getCause().getCause())) {
                    return e.getCause().getCause().getMessage();
                }
                result = e;
            }
            if (max_retry < 1) {
                return result;
            }

            // 下面表示出错了需要重试
            Supplier<Result> callMethod = () -> {
                System.out.println("call method in retry!!!");
                Result resultbulk = new Result();
                try {
                    Object object = context.proceed();
                    resultbulk.error = null;
                    resultbulk.issuccess = true;
                    resultbulk.result = object;
                } catch (Throwable e) {
                    resultbulk.issuccess = false;
                    resultbulk.result = null;
                    resultbulk.error = e;
                    resultbulk.isRetry = false;
                }
                if (resultbulk.issuccess) {
                    return resultbulk;
                } else {
                    if (!isAbortOn(retry, resultbulk.error) && isRetryOn(retry, resultbulk.error)) {
                        resultbulk.isRetry = true;
                    }
                }
                return resultbulk;
            };


            Callable<Result> callable = () -> {

                // 第一次先调用一次，如果失败了则进行重试
                Result resultbulk = callMethod.get();
                if (resultbulk.issuccess) {
                    return resultbulk;
                }


                Result innerResultBulk = null;
                for (int i = 0; i < max_retry; i++) {
                    System.out.println("retry times:" + i);
                    ScheduledFuture<Result> future = executorService.schedule(() -> {
                        return callMethod.get();
                    }, getDelayTime(delay, jitter), TimeUnit.MILLISECONDS);
                    innerResultBulk = future.get();
                    if (innerResultBulk.issuccess)
                        return innerResultBulk;
                    if (!innerResultBulk.isRetry) {
                        System.out.println("error not retry");
                        break;
                    }
                }

                return innerResultBulk;
            };


            Future<Result> future = executorService.submit(callable);
            try {
                Result finalResult = future.get(max_duration, TimeUnit.MILLISECONDS);
                if (finalResult.issuccess)
                    return finalResult.result;
                else
                    return finalResult.error.getMessage();
            } catch (TimeoutException e) {
                // 这里表示方法调用已经超时，可以进行捕获处理
                future.cancel(true);
            }
        }
        return context.proceed();
    }

    private long getDelayTime(long delay, long jitter) {
        return delay + ThreadLocalRandom.current().nextLong(Math.negateExact(jitter), jitter);
    }


    private Annotation findAnnotation(Method method, Class<? extends Annotation> clazz) {
        Annotation annotation = method.getAnnotation(clazz);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(clazz);
        }
        return annotation;
    }

    private boolean isAbortOn(Retry retry, Throwable e) {
        boolean abort = false;
        for (Class<? extends Throwable> abortType : retry.abortOn()) {
            if (abortType.isInstance(e.getCause())) {
                abort = true;
                break;
            }
        }
        return abort;
    }

    private boolean isRetryOn(Retry retry, Throwable e) {
        boolean retryOn = false;
        for (Class<? extends Throwable> retryType : retry.retryOn()) {
            if (retryType.isInstance(e.getCause())) {
                retryOn = true;
                break;
            }
        }
        return retryOn;
    }


    private class Result {
        Object result = null;
        boolean issuccess = false;
        Throwable error = null;
        boolean isRetry = true;
    }

}
