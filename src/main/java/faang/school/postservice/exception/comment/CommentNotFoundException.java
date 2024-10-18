package faang.school.postservice.exception.comment;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApiException {
    public CommentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
