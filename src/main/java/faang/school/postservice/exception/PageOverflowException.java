package faang.school.postservice.exception;

public class PageOverflowException extends RuntimeException {

    public PageOverflowException(String message, Object... args) {
        super(String.format(message, args));
    }
}
