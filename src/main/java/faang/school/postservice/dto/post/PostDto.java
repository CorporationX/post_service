package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        Long id,

        String content,
        boolean published,
        LocalDateTime publishedAt,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        @NotBlank(message = "post author cannot be empty")
        Long authorId,

        @NotBlank(message = "project for the post cannot be empty")
        Long projectId
) {
}
