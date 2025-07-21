package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreatePostDto(
        @NotBlank
        String content,
        Long authorId,
        Long projectId
) {
}
