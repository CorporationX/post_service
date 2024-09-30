package faang.school.postservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
