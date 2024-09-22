package faang.school.postservice.exception;

public class UnknownException extends RuntimeException {
    public UnknownException(String format, Object... args) {
        super(String.format(format, args));
    }
}
