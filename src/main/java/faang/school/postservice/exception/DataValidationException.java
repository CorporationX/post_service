package faang.school.postservice.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(ErrorMessage messageError) {
        super(messageError.getMessage());
    }

    public DataValidationException(String message) {
        super(message);
    }
}
