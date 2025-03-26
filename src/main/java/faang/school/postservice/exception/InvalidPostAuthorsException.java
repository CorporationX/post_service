package faang.school.postservice.exception;

public class InvalidPostAuthorsException extends RuntimeException {
    public InvalidPostAuthorsException(String message) {
        super(message);
    }
}
