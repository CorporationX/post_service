package faang.school.postservice.exception;

public class LikeOptimisticLockException extends RuntimeException {
    public LikeOptimisticLockException(String message) {
        super(message);
    }
}
