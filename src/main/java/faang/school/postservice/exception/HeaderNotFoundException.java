package faang.school.postservice.exception;

public class HeaderNotFoundException extends RuntimeException {
    public HeaderNotFoundException(String message) {
        super(message);
    }
}
