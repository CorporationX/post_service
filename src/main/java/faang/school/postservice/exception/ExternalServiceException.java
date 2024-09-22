package faang.school.postservice.exception;

public class ExternalServiceException extends RuntimeException{
    public ExternalServiceException(String format, Object... args) {
        super(String.format(format, args));
    }
}
