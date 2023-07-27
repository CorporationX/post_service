package faang.school.postservice.exception;

public class NoPostInDataBaseException extends RuntimeException {

    public NoPostInDataBaseException(String message) {
        super(message);
    }
}
