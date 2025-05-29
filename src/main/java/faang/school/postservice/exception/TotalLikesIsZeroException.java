package faang.school.postservice.exception;

public class TotalLikesIsZeroException extends RuntimeException {
    public TotalLikesIsZeroException(String entityName, Long entityId) {
        super("Total likes for %s with ID %d is zero.".formatted(entityName, entityId));
    }
}
