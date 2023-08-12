package faang.school.postservice.exception;

public class DataAlreadyExistingException extends DataValidationException {
    public DataAlreadyExistingException(String message) {
        super(message);
    }
}
