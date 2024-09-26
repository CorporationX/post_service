package faang.school.postservice.exception.handler;

import lombok.Getter;

import java.util.Map;

@Getter
public class ConstraintViolationException extends RuntimeException {
    private final Map<String, String> fieldErrors;

    public ConstraintViolationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
