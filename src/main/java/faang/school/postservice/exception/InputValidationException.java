package faang.school.postservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InputValidationException extends RuntimeException {
    private final Map<String, String> errorFields;

    public InputValidationException(Map<String, String> errorFields) {
        this.errorFields = errorFields;
    }
}
