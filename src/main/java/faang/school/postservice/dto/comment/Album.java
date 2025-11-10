package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record Album(
        Long id,

        @NotBlank(message = "Title cannot be empty")
        String title,

        @NotBlank(message = "Description cannot be empty")
        String description,

        @NotNull(message = "Author ID cannot be null")
        Long authorId,

        List<Post> posts,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {
}