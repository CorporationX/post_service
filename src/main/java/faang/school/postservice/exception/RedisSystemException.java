package faang.school.postservice.exception;

public class RedisSystemException extends RuntimeException {
    public RedisSystemException(String message) {
        super(message);
    }
}
