package faang.school.postservice.exception.post.image;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class DownloadImageToPostException extends ApiException {
    private static final String MESSAGE = "Image %d failed to download";

    public DownloadImageToPostException(Long resourceId) {
        super(MESSAGE.formatted(resourceId), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
