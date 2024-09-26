package faang.school.postservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final Map<String, String> fieldErrors;

    public EntityNotFoundException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
}
