package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record PostFeedDto(
        @NotNull(message = "Post ID cannot be null")
        Long id,
        @NotBlank(message = "Content cannot be blank")
        String content,
        @NotNull(message = "Published timestamp cannot be null")
        LocalDateTime publishedAt,
        @PositiveOrZero(message = "Like count cannot be negative")
        Long likeCount,
        @PositiveOrZero(message = "Comment count cannot be negative")
        Long commentCount,
        @NotNull(message = "Author ID cannot be null")
        Long authorId
) {
}
