package faang.school.postservice.dto.comment.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponseComment(
        Long id,

        @NotBlank(message = "Content cannot be empty")
        @Size(max = 4096, message = "Content cannot exceed 4096 characters")
        String content,

        @NotNull(message = "Author ID cannot be null")
        Long authorId,

        @NotNull(message = "Post ID cannot be null")
        Long postId,

        String largeImageFileKey,
        String smallImageFileKey,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}