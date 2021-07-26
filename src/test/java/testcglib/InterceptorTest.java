package testcglib;

import com.interceptor.BulkHeadInterceptor;
import com.interceptor.InterceptorEnhancer;
import com.interceptor.RetryInterceptor;
import com.interceptor.TimeoutInterceptor;
import com.interceptorAPI.ReflectiveMethodInvocationContext;
import org.junit.Test;
import testcglib.EchoService;
import testcglib.EchoService2;

import java.lang.reflect.Method;

public class InterceptorTest {

    private BulkHeadInterceptor interceptor = new BulkHeadInterceptor();
    private BulkHeadInterceptor interceptor2 = new BulkHeadInterceptor();

    @Test
    public void testInThreadIsolation() throws Exception {

        /*
        关于注解的对象实例的问题：
        在BulkheadInterceptor中的线程池和信号量的创建中，为什么使用ConcurrentMap<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<>();
        用Bulkhead的实例来做key呢，但经过我的测试

        即便我创建了两个实例，使用这两个实例对象
        EchoService echoService = new EchoService();
        EchoService echoService2 = new EchoService();
        来创建两个ReflectiveMethodInvocationContext这个对象，最后调用的时候虽然context实例不同，但是获取到的注解解析的实例对象是同一个生成的实例，
        这么说来就是annotation对象是根据方法来的，是不是应该一个方法如果需要限流用到了Bulkhead这个注解，或者一个类加上了Bulkhead注解，那么这个方法或者类就对应了一个线程池

        可以按照这个思路来修改 把 private final Map<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<Bulkhead, ExecutorService>();
        中的Buldhead修改成Method或者Class么？
        */

        EchoService echoService = new EchoService();

        EchoService echoService2 = new EchoService();
        EchoService2 echoService3 = new EchoService2();

        Method method = EchoService.class.getMethod("echo", Object.class);
        Method method2 = EchoService.class.getMethod("echo123", Integer.class);
        Method method3 = EchoService2.class.getMethod("echo123", Integer.class);
        ReflectiveMethodInvocationContext context = new ReflectiveMethodInvocationContext
                (echoService, method, "Hello,World");

        ReflectiveMethodInvocationContext context2 = new ReflectiveMethodInvocationContext
                (echoService2, method2, 123);
        ReflectiveMethodInvocationContext context3 = new ReflectiveMethodInvocationContext
                (echoService3, method3, 123);

        interceptor.execute(context);
        interceptor.execute(context2);
        interceptor.execute(context3);
        interceptor.execute(context);
        interceptor.execute(context);
        interceptor2.execute(context2);

    }

    @Test
    public void testInSemaphoreIsolation() throws Exception {
        EchoService echoService = new EchoService();
        Method method = EchoService.class.getMethod("echo", String.class);
        ReflectiveMethodInvocationContext context = new ReflectiveMethodInvocationContext
                (echoService, method, "Hello,World");
        interceptor.execute(context);
    }

    @Test
    public void testRetry() throws Exception {
        EchoService2 echoService = new EchoService2();
        BulkHeadInterceptor bulkHeadInterceptor = new BulkHeadInterceptor();
        TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor();
        RetryInterceptor retryInterceptor = new RetryInterceptor();
        EchoService2 object = (EchoService2) new InterceptorEnhancer().enhanceCGLib(echoService, retryInterceptor, bulkHeadInterceptor, timeoutInterceptor);
        System.out.println(object.echo("Hello,World"));
    }

    @Test
    public void testRetry2() throws Exception {
        EchoService2 echoService = new EchoService2();
        BulkHeadInterceptor bulkHeadInterceptor = new BulkHeadInterceptor();
        TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor();
        RetryInterceptor retryInterceptor = new RetryInterceptor();
        EchoService2 object = (EchoService2) new InterceptorEnhancer().enhanceCGLib(echoService, retryInterceptor, bulkHeadInterceptor, timeoutInterceptor);
        System.out.println(object.echo2("Hello,World"));
    }



}
