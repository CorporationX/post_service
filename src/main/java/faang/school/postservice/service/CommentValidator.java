package faang.school.postservice.service;

import faang.school.postservice.exception.FileFormatException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.utils.ImageType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Service
public class CommentValidator {

    @Value("${comment.image.max-file-size}")
    private String maxFileSize;

    public void validateCommentUpdate(Comment updatedComment) {
        if (updatedComment.getId() != null) {
            throw new IllegalArgumentException("You can't modify id of comment");
        }

        if (updatedComment.getAuthorId() != null) {
            throw new IllegalArgumentException("You can't modify author id of comment");
        }

        if (updatedComment.getLargeImageFileKey() != null) {
            throw new IllegalArgumentException("You can't modify large image file key of comment");
        }

        if (updatedComment.getSmallImageFileKey() != null) {
            throw new IllegalArgumentException("You can't modify small image file key of the comment");
        }
    }

    public void validateAuthor(Comment comment, Long userId) {
        if (!Objects.equals(userId, comment.getAuthorId())) {
            throw new IllegalArgumentException("You can't modify comment of another user");
        }
    }

    public void validateImageSize(MultipartFile image) {
        maxFileSize = maxFileSize.toUpperCase();
        if (!maxFileSize.endsWith("MB")) {
            throw new IllegalArgumentException("Max file size should be in MB");
        }
        long maxSizeBytes = Long.parseLong(maxFileSize.replaceAll("[^0-9]", "")) * 1024 * 1024;
        if (image.getSize() > maxSizeBytes) {
            throw new FileFormatException("Uploaded file " + image.getOriginalFilename() + " is larger than " + maxFileSize);
        }
    }

    public void validateImageFormat(MultipartFile image) {
        String imageType = image.getContentType();
        boolean isValid = Arrays.stream(ImageType.values()).anyMatch((type) ->
                Objects.equals(imageType, type.getMimeType()));
        if (!isValid) {
            String validFormats = Arrays.stream(ImageType.values())
                    .map(ImageType::getMimeType)
                    .collect(Collectors.joining(", "));

            throw new FileFormatException(String.format(
                    "Invalid image format: %s. Supported formats are: %s",
                    imageType, validFormats
            ));
        }
    }

}
