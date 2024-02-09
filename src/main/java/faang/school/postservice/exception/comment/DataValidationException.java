package faang.school.postservice.exception.comment;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}