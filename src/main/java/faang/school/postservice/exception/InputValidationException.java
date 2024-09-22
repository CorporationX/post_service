package faang.school.postservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InputValidationException extends RuntimeException{
    private final Map<String, String> map;
    public InputValidationException(Map<String, String> map) {
        this.map = map;
    }
}
