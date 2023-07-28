package faang.school.postservice.exceptions;

public class DataAlreadyExistingException extends RuntimeException{
    public DataAlreadyExistingException(String message) {
        super(message);
    }
}
