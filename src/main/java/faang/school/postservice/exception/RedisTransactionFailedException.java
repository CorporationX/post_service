package faang.school.postservice.exception;

public class RedisTransactionFailedException extends RuntimeException {
    public RedisTransactionFailedException(String message) {
        super(message);
    }
}
