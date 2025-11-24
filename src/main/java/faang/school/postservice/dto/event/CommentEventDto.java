package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CommentEventDto(
        @NotNull
        Long postId,
        @NotNull
        Long authorId,
        @NotNull
        Long receivedId,
        @NotNull
        Long commentId,
        @NotNull
        LocalDateTime createdAt
) {
}
