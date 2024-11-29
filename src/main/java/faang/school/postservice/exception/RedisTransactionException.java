package faang.school.postservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RedisTransactionException extends RuntimeException {
    public RedisTransactionException(String message) {
        super(message);
    }
}
