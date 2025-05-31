package faang.school.postservice.exception;

public class RedisUnavailableException extends RuntimeException {

    public RedisUnavailableException(String message, Throwable cause) {
        super(message);
    }

    public RedisUnavailableException(String message) {
        super(message);
    }

    public RedisUnavailableException(Throwable cause) {
        super(cause);
    }
}
