package faang.school.postservice.exception.post;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApiException {
    private static final String MESSAGE = "Post Not Found";

    public PostNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
