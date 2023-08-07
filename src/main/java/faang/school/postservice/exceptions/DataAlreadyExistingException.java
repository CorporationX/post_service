package faang.school.postservice.exceptions;

public class DataAlreadyExistingException extends DataValidationException{
    public DataAlreadyExistingException(String message) {
        super(message);
    }
}
