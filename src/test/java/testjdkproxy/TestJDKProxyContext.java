package testjdkproxy;

import com.interceptor.BulkHeadInterceptor;
import com.interceptor.InterceptorEnhancer;
import com.interceptor.RetryInterceptor;
import com.interceptor.TimeoutInterceptor;
import org.junit.Test;

public class TestJDKProxyContext {

    @Test
    public void testJDKProxyContext() {

        //TODO 再改进就使用spi的方式做一个扫描注解的类，将包下所有包含@Inteceptor 注解的类都进行加载
        // 实现动态加载 Inteceptor
        BulkHeadInterceptor bulkHeadInterceptor = new BulkHeadInterceptor();
        TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor();
        RetryInterceptor retryInterceptor = new RetryInterceptor();
        EchoService service = new EchoServiceImpl();
        EchoService object = (EchoService) new InterceptorEnhancer().enhanceJDKProxy(service, bulkHeadInterceptor, timeoutInterceptor, retryInterceptor);
        System.out.println(object.echo("Hello,World"));
    }
}
