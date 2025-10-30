package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        @NotNull(message = "The content must exist")
        @NotBlank(message = "The content cannot be blank")
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt,
        boolean deleted
) {
}
