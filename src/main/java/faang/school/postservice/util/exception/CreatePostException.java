package faang.school.postservice.util.exception;

public class CreatePostException extends RuntimeException{
    public CreatePostException() {
    }

    public CreatePostException(String message) {
        super(message);
    }
}
