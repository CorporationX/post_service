package faang.school.postservice.exception.post;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApiException {
    private static final String MESSAGE = "Post id=%d Not Found";

    public PostNotFoundException(Long id) {
        super(MESSAGE.formatted(id), HttpStatus.NOT_FOUND);
    }
}
