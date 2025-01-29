package faang.school.postservice.exceptions;

public class PostWasNotFoundException extends RuntimeException {
    public PostWasNotFoundException(String message) {
        super(message);
    }
}
