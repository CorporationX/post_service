package faang.school.postservice.validation.resource;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class ResourceValidator {

    @Value("${media.image.max-file-size}")
    private long maxImageSize;

    @Value("${media.audio-video.max-file-size}")
    private long maxVideoOrAudioSize;

    public void validateImageFileSize(MultipartFile file) {
        if (maxImageSize * 1024 * 1024 < file.getSize()) {
            throw new DataValidationException(String.format("Image file size is too large. Max image size is %d MB", maxImageSize));
        }
    }

    public void validateAudioOrVideoFileSize(MultipartFile file) {
        if (maxVideoOrAudioSize * 1024 * 1024 < file.getSize()) {
            throw new DataValidationException(String.format("Media file size is too large. Max media file size is %d MB", maxVideoOrAudioSize));
        }
    }

    public void validateTypeAudioOrVideo(MultipartFile mediaFile) {
        if (!isAudio(mediaFile) && !isVideo(mediaFile)) {
            throw new DataValidationException("Invalid media type: " + mediaFile.getContentType());
        }
    }
    
    private boolean isAudio(MultipartFile mediaType) {
        String type = Objects.requireNonNullElse(mediaType.getContentType(), "").toLowerCase();
        return type.contains("audio");
    }
    
    private boolean isVideo(MultipartFile mediaType) {
        String type = Objects.requireNonNullElse(mediaType.getContentType(), "").toLowerCase();
        return type.contains("video");
    }
}
