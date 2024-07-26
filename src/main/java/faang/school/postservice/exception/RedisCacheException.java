package faang.school.postservice.exception;

public class RedisCacheException extends RuntimeException {
    public RedisCacheException(String message) {
        super(message);
    }
}
