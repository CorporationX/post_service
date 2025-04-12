package faang.school.postservice.exception;

public class PostUnverifiedException extends RuntimeException {

    public PostUnverifiedException(String message, Object... args) {
        super(String.format(message, args));
    }
}
