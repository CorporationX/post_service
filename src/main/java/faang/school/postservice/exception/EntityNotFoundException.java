package faang.school.postservice.exception;

public class EntityNotFoundException extends IllegalArgumentException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
