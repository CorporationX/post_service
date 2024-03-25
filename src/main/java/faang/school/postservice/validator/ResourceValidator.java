package faang.school.postservice.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceValidator {

    @Value("${post.content_to_post.max_amount}")
    private int maxAmountFiles;
    @Value("${post.content_to_post.max_size}")
    private int maxFileSize;
    @Value("${multipart.max_video_file_size}")
    private int maxVideoSize;
    private static final Set<String> ALLOWED_VIDEO_MIME_TYPES = Set.of("video/mpeg", "video/mp4", "video/x-msvideo");

    public void validateFileSize(long fileSize) {
        if (fileSize > maxFileSize) {
            throw new IllegalArgumentException("Size of file must be equals or less than 5 mb");
        }
    }

    public void validateFilesAmount(int existFilesAmount, int newFilesAmount) {
        if (existFilesAmount + newFilesAmount > maxAmountFiles) {
            String exceptionMsg = String.format("You can upload only 10 files or less. Exist files = %s. New files = %s"
                    , existFilesAmount, newFilesAmount);
            throw new IllegalArgumentException(exceptionMsg);
        }
    }

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
            throw new IllegalArgumentException("The video has not allowed format.");
        }
    }

}
