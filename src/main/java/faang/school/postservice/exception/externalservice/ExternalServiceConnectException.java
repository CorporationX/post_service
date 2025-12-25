package faang.school.postservice.exception.externalservice;

public class ExternalServiceConnectException extends ExternalServiceException {
    public ExternalServiceConnectException(String service, String message) {
        super(service, message);
    }

    public ExternalServiceConnectException(String service, String message, Throwable cause) {
        super(service, message, cause);
    }
}