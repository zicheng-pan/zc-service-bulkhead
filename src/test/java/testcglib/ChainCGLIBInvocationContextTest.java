package testcglib;

import com.interceptor.BulkHeadInterceptor;
import com.interceptor.InterceptorEnhancer;
import com.interceptor.TimeoutInterceptor;
import com.interceptorAPI.CglibInvocationContext;
import com.interceptorAPI.ChainableInvocationContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;
import testcglib.DefaultEchoServiceinf;
import testcglib.EchoServiceinf;

import java.lang.reflect.Method;

public class ChainCGLIBInvocationContextTest {
    @Test
    public void testInchainInvokeContext1() throws Exception {
        Enhancer enhancer = new Enhancer();
        Class<?> superClass = DefaultEchoServiceinf.class;
        enhancer.setSuperclass(superClass);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object source, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                CglibInvocationContext context = new CglibInvocationContext(source, method, methodProxy, args);
                BulkHeadInterceptor bulkHeadInterceptor = new BulkHeadInterceptor();
                TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor();
                ChainableInvocationContext chainContext = new ChainableInvocationContext(context, bulkHeadInterceptor, timeoutInterceptor);
                return chainContext.proceed();
            }
        });

        // 创建代理对象
        EchoServiceinf echoServiceinf = (EchoServiceinf) enhancer.create();
        // 输出执行结果
        System.out.println(echoServiceinf.echo("Hello,World"));
    }

    @Test
    public void testInchainInvokeContext2() throws Exception {
        BulkHeadInterceptor bulkHeadInterceptor = new BulkHeadInterceptor();
        TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor();
        EchoServiceinf service = new DefaultEchoServiceinf();
        EchoServiceinf object = (EchoServiceinf) new InterceptorEnhancer().enhanceCGLib(service, bulkHeadInterceptor, timeoutInterceptor);
        System.out.println(object.echo("Hello,World"));
    }
}
