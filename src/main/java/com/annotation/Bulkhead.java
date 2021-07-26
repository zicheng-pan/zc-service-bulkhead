package com.annotation;

/*
   限流： 可以通过线程池和信号量两种方式来限流 通过@Asynchronous 注解那么就是线程隔离，没有这个注解，那么就是信号量隔离
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Bulkhead {

    int value() default 10;

    int waitingTaskQueue() default 10;
}
