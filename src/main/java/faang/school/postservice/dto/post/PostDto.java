package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        long id,

        @NotBlank
        String content,

        Long authorId,

        Long projectId,

        boolean published,

        LocalDateTime publishedAt,

        LocalDateTime scheduledAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}
