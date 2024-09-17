package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        @NotBlank(message = "Content cannot be empty")
        @Size(max = 4096, message = "Content cannot exceed 4096 characters")
        String content,
        @NotNull(message = "Author ID cannot be null")
        Long authorId,
        @NotNull(message = "Post ID cannot be null")
        Long postId
) {
}