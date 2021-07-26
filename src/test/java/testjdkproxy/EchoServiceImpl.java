package testjdkproxy;

public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String message) {
        return message;
    }
}
