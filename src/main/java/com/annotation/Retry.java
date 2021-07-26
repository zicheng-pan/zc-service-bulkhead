package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
   重试：用于失败后的重试
   把时间单位去掉了，默认都是毫秒，这样方便统计
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retry {


    /**
     * The max number of the retries.
     *
     * @return the max number of retries. -1 means retry forever. The value must be greater than or equal to -1.
     */
    int maxRetries() default 3;

    /**
     * The delay between retries. Defaults to 0. The value must be greater than or equal to 0.
     *
     * @return the delay time
     */
    long delay() default 0;

    /**
     * The unit for {@link #delay}. Defaults to {@link java.time.temporal.ChronoUnit#MILLIS} if not set.
     *
     * @return the delay unit
     */
//    ChronoUnit delayUnit() default ChronoUnit.MILLIS;

    /**
     * The max duration. The max duration must be greater than the delay duration if set. 0 means not set.
     *
     * @return the maximum duration to perform retries for
     */
    long maxDuration() default 180000;

    /**
     * The duration unit for {@link #maxDuration}. Defaults to {@link java.time.temporal.ChronoUnit#MILLIS} if not set.
     *
     * @return the duration unit
     */
//    ChronoUnit durationUnit() default ChronoUnit.MILLIS;

    /**
     * <p>
     * Set the jitter to randomly vary retry delays for. The value must be greater than or equals to 0.
     * 0 means not set.
     * </p>
     * The effective delay will be [delay - jitter, delay + jitter] and always greater than or equal to 0.
     * Negative effective delays will be 0.
     *
     * @return the jitter that randomly vary retry delays by. e.g. a jitter of 200 milliseconds
     * will randomly add between -200 and 200 milliseconds to each retry delay.
     */
    long jitter() default 200;

    /**
     * The delay unit for {@link #jitter}. Defaults to {@link java.time.temporal.ChronoUnit#MILLIS} if not set.
     *
     * @return the jitter delay unit
     */
//    ChronoUnit jitterDelayUnit() default ChronoUnit.MILLIS;


    /**
     * The list of exception types that should trigger a retry.
     * <p>
     * Note that if a method throws a {@link Throwable} which is not an {@link Error} or {@link Exception}, non-portable behavior results.
     *
     * @return the exception types on which to retry
     */
    Class<? extends Throwable>[] retryOn() default {Exception.class};

    /**
     * The list of exception types that should <i>not</i> trigger a retry.
     * <p>
     * This list takes priority over the types listed in {@link #retryOn()}.
     * <p>
     * Note that if a method throws a {@link Throwable} which is not an {@link Error} or {@link Exception}, non-portable behavior results.
     *
     * @return the exception types on which to abort (not retry)
     */
    Class<? extends Throwable>[] abortOn() default {};

}
