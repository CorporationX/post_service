package faang.school.postservice.exception.post.image;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class DeleteImageToPostException extends ApiException {
    private static final String MESSAGE = "Image %d failed to delete from post %d";

    public DeleteImageToPostException(Long resourceId, Long postId) {
        super(MESSAGE.formatted(resourceId, postId), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
