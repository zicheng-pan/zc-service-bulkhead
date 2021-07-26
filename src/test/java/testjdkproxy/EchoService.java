package testjdkproxy;

import com.annotation.Bulkhead;
import com.annotation.Retry;
import com.annotation.Timeout;

public interface EchoService {

    @Retry
    @Timeout
    @Bulkhead
    public String echo(String message);
}
