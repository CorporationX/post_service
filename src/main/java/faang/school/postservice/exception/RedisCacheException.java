package faang.school.postservice.exception;

public class RedisCacheException extends RuntimeException {

    public RedisCacheException(String message) {
        super(message);
    }

    public RedisCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisCacheException(Throwable cause) {
        super(cause);
    }
}
