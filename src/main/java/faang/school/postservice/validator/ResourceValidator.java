package faang.school.postservice.validator;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ResourceValidator {

    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100Mb
    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of("video/mpeg", "video/mp4", "video/x-msvideo");

    public void videoIsValid(MultipartFile file) {
        isAllowedVideoType(file.getContentType());
        sizeVideoValid(file.getSize());
    }

    private void sizeVideoValid(long size) {
        if (size > MAX_VIDEO_SIZE) {
            throw new MaxUploadSizeExceededException(size);
        }
    }

    private void isAllowedVideoType(String type) {
        if (!ALLOWED_VIDEO_MIME_TYPES.contains(type)) {
            throw new IllegalArgumentException("Type file not valid");
        }
    }

}
