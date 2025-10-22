package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PostUpdateDto(
        @Schema(description = "Контент для поста", example = "мой первый пост")
        @NotBlank(message = "the content is irrelevant or an empty string ")
        String content
) {
}
