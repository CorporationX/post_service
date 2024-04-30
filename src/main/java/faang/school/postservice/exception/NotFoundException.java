package faang.school.postservice.exception;

/**
 * @author Alexander Bulgakov
 */

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
