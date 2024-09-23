package faang.school.postservice.exception;

public class DataSavingException extends RuntimeException {
    public DataSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
