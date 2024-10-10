package faang.school.postservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    private static final String MESSAGE = "Resource id=%d Not Found";

    public ResourceNotFoundException(Long id) {
        super(MESSAGE.formatted(id), HttpStatus.NOT_FOUND);
    }
}
