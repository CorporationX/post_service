package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateCommentDto(
        @NotNull(message = "Comment author ID cannot be null")
        Long authorId,
        @NotNull(message = "Comment post ID cannot be null")
        Long postId,
        @NotBlank(message = "Comment content cannot be blank")
        @Size(max = 4096, message = "Comment text cannot exceed 4096 characters")
        String content,
        @NotNull(message = "Creation time cannot be absent")
        LocalDateTime createdAt
) {
}