package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Resource(
        Long id,

        @NotNull(message = "Post ID cannot be null")
        Long postId,

        @NotBlank(message = "File key cannot be empty")
        String fileKey,

        @NotBlank(message = "File type cannot be empty")
        String fileType,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {
}