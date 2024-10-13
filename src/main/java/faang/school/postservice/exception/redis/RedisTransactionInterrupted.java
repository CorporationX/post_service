package faang.school.postservice.exception.redis;

public class RedisTransactionInterrupted extends RuntimeException {
    public RedisTransactionInterrupted(String message, Object... args) {
        super(String.format(message, args));
    }
}
