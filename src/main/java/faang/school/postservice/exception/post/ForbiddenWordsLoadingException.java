package faang.school.postservice.exception.post;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ForbiddenWordsLoadingException extends ApiException {
    private static final String MESSAGE = "Forbidden words loading error";

    public ForbiddenWordsLoadingException() {
        super(MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
