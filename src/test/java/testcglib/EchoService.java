package testcglib;

import com.annotation.Asynchronous;
import com.annotation.Bulkhead;
import com.annotation.Timeout;


public class EchoService {

    @Timeout
    public void echo(String message) {
        echo((Object) message);
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
