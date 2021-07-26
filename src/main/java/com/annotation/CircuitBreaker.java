package com.annotation;
/*
   断路器 --》用固定窗口 统计个数 或者滑动时间窗口来统计服务调用成功和失败 失败/总调用数
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
}
