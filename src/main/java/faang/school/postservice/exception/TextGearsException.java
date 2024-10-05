package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class TextGearsException extends RuntimeException {
    public TextGearsException(String message) {
        super(message);
    }
}
