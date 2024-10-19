package faang.school.postservice.exception.post;

public class ImmutablePostDataException extends RuntimeException {
    public ImmutablePostDataException(String message) {
        super(message);
    }
}
