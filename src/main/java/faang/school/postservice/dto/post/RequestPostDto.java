package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RequestPostDto(
        @NotNull(message = "Content must not be null")
        @Size(max = 4096, message = "Content must not exceed 4096 characters")
        String content,

        @NotNull(message = "Project ID must not be null")
        @Positive(message = "Project ID must be a positive number")
        Long projectId
) {
}
