package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PostDto(
        Long id,

        @NotBlank(message = "Title can not be null or empty")
        @Size(min = 1, max = 150)
        String title,

        @NotBlank(message = "Content can not be null or empty")
        @Size(min = 1, max = 4096)
        String content,

        Long authorId,
        Long projectId
) {
}
