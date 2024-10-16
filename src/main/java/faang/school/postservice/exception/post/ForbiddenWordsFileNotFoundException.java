package faang.school.postservice.exception.post;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ForbiddenWordsFileNotFoundException extends ApiException {
    private static final String MESSAGE = "Forbidden words file not found";

    public ForbiddenWordsFileNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
