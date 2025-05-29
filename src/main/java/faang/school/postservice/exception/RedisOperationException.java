package faang.school.postservice.exception;

public class RedisOperationException extends RuntimeException {
    public RedisOperationException(String message) {
        super(message);
    }
}
