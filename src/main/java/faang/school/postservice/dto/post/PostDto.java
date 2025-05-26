package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostDto(
        Long id,
        @NotBlank(message = "Содержимое поста не может быть пустым")
        String content,
        Long authorId,
        Long projectId
) {
}
