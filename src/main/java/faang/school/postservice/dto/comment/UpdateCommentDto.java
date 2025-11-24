package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateCommentDto(
        @NotNull(message = "Comment ID cannot be null")
        Long id,
        @NotNull(message = "Comment author ID cannot be null")
        Long authorId,
        @NotNull(message = "Comment post ID cannot be null")
        Long postId,
        @NotBlank(message = "Comment text cannot be blank")
        @Size(max = 4096, message = "Comment text cannot exceed 4096 characters")
        String content,
        @NotNull(message = "Update time cannot be absent")
        LocalDateTime updatedAt
) {
}