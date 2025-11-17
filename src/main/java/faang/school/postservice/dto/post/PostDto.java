package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Future;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt,
        @Future(message = "Scheduled time must be in the future")
        LocalDateTime scheduledAt,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
