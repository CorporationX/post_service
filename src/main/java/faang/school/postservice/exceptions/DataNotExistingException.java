package faang.school.postservice.exceptions;

public class DataNotExistingException extends RuntimeException{
    public DataNotExistingException(String message) {
        super(message);
    }
}
