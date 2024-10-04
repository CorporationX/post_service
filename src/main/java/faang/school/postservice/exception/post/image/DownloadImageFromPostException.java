package faang.school.postservice.exception.post.image;

import faang.school.postservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class DownloadImageFromPostException extends ApiException {
    private static final String MESSAGE = "Image %d failed to download";

    public DownloadImageFromPostException(Long resourceId) {
        super(MESSAGE.formatted(resourceId), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
