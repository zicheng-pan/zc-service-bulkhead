package testcglib;

import com.annotation.Asynchronous;
import com.annotation.Bulkhead;
import com.annotation.Retry;


public class EchoService2 {

    @Retry(delay = 1000, retryOn = {UnsupportedOperationException.class})
    public String echo(String message) {
        throw new UnsupportedOperationException("not supported");
    }

    @Retry(delay = 1000, retryOn = {})
    public String echo2(String message) {
        throw new UnsupportedOperationException("not supported");
    }

    @Asynchronous
    @Bulkhead(value = 1)
    public void echo(Object message) {
        System.out.println(message);
    }

//
//    @Retry(maxRetries = 3,
//            delay = 0, maxDuration = 0, jitter = 0,
//            retryOn = UnsupportedOperationException.class)
//    public void echo(Long value) {
//        throw new UnsupportedOperationException();
//    }

    @Asynchronous
    @Bulkhead(value = 1)
    public void echo123(Integer message) {
        System.out.println(message);
    }
}
