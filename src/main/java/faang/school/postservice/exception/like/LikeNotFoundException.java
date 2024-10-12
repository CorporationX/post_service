package faang.school.postservice.exception.like;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class LikeNotFoundException  extends ApiException {
    public LikeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
