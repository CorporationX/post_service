package faang.school.postservice.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ResourceValidator {

    @Value("${multipart.max_video_file_size}")
    private int maxVideoSize;
    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of("video/mpeg", "video/mp4", "video/x-msvideo");

    public void videoIsValid(MultipartFile file) {
        isAllowedVideoType(file.getContentType());
        sizeVideoValid(file.getSize());
    }

    private void sizeVideoValid(long size) {
        if (size > maxVideoSize) {
            throw new MaxUploadSizeExceededException(size);
        }
    }

    private void isAllowedVideoType(String type) {
        if (!ALLOWED_VIDEO_MIME_TYPES.contains(type)) {
            throw new IllegalArgumentException("Type file not like video");
        }
    }

}
