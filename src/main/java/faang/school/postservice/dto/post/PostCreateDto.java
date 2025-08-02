package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record PostCreateDto(
    @NotBlank(message = "The content cannot be empty")
    String content,
    @Positive(message = "The user's ID must be positive.")
    Long authorId,
    @Positive(message = "The project's ID must be positive.")
    Long projectId
) {
}
