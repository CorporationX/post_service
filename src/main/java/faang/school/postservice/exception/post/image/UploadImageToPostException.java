package faang.school.postservice.exception.post.image;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UploadImageToPostException extends ApiException {
    private static final String MESSAGE = "Image %s failed to upload to post %d";

    public UploadImageToPostException(String imageName, Long postId) {
        super(MESSAGE.formatted(imageName, postId), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
