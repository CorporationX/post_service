package faang.school.postservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
