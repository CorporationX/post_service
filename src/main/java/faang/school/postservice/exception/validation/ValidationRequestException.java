package faang.school.postservice.exception.validation;

public class ValidationRequestException extends RuntimeException {
    public ValidationRequestException(String msg) {
        super(msg);
    }
}
