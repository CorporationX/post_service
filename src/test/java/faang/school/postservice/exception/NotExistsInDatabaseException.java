package faang.school.postservice.exception;

public class NotExistsInDatabaseException extends RuntimeException{
    public NotExistsInDatabaseException(String message) {
        super(message);
    }
}
