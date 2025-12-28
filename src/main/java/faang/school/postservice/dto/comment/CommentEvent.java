package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CommentEvent(
        @NotNull(message = "Author ID cannot be null")
        Long authorId,
        @NotNull(message = "Post ID cannot be null")
        Long postId,
        @NotNull(message = "Comment ID cannot be null")
        Long commentId,
        @NotBlank(message = "Content cannot be blank")
        String content,
        LocalDateTime createdAt
) {
}
