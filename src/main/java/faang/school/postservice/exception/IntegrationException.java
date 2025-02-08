package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public class IntegrationException extends RuntimeException {

    public IntegrationException(String message) {
        super(message);
    }
}
