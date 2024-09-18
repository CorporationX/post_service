package faang.school.postservice.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException() {
        super("data validation failed");
    }
}
