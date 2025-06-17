package faang.school.postservice.exception;

public class PostDtoValidationException extends RuntimeException {
    public PostDtoValidationException(String message) {
        super(message);
    }
}