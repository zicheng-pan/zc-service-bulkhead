package testcglib;

import com.annotation.Timeout;

public class DefaultEchoServiceinf implements EchoServiceinf {

    @Override
    @Timeout
    public String echo(String s) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s;
    }
}
