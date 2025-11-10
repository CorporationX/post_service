package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record Post(
        Long id,

        @NotBlank(message = "Content cannot be empty")
        @Size(max = 4096, message = "Content cannot exceed 4096 characters")
        String content,

        @NotNull(message = "Author ID cannot be null")
        Long authorId,

        Long projectId,

        List<Album> albums,
        Ad ad,
        List<Resource> resources,

        boolean published,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        boolean deleted,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}