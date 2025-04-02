package faang.school.postservice.service.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages {
    FAILED_READ("Failed to read uploaded file"),
    IMAGE_SIZE_EXCEED("Image size must not exceed 5MB"),
    INVALID_TYPE("Invalid file type: not an image"),
    INVALID_FORMAT("Only JPG and PNG formats are supported"),
    FAILED_RESIZE("Failed to resize image");

    private final String message;
}
