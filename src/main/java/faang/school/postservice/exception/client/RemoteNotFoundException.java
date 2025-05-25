package faang.school.postservice.exception.client;

public class RemoteNotFoundException extends RuntimeException {
    public RemoteNotFoundException(String msg) {
        super(msg);
    }
}
