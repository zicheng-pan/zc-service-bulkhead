/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.interceptor;


import com.interceptorAPI.CglibInvocationContext;
import com.interceptorAPI.ChainableInvocationContext;
import com.interceptorAPI.JavaInterfaceInvocationContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterceptorEnhancer {

    public Object enhanceCGLib(Object target, Object... interceptors) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object source, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                CglibInvocationContext context = new CglibInvocationContext(source, method, methodProxy, args);
                ChainableInvocationContext chainContext = new ChainableInvocationContext(context, interceptors);
                return chainContext.proceed();
            }
        });
        return enhancer.create();
    }

    public <T> Object enhanceJDKProxy(T obj, Object... interceptores) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        JavaInterfaceInvocationContext context = new JavaInterfaceInvocationContext(obj, method, args);
                        ChainableInvocationContext chainContext = new ChainableInvocationContext(context, interceptores);
                        return chainContext.proceed();
                    }
                });
    }

}
