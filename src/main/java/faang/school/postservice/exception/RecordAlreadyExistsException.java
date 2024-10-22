package faang.school.postservice.exception;

import org.springframework.http.HttpStatus;

public class RecordAlreadyExistsException extends ApiException {
    public RecordAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
