package faang.school.postservice.exception.post;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PostPublishedException extends ApiException {
    private static final String MESSAGE = "Post id=%d already published";

    public PostPublishedException(Long id) {
        super(MESSAGE.formatted(id), HttpStatus.CONFLICT);
    }
}
