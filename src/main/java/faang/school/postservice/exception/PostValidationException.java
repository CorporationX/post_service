package faang.school.postservice.exception;

public class PostValidationException extends RuntimeException{
    public PostValidationException(String message) {
        super(message);
    }
}
