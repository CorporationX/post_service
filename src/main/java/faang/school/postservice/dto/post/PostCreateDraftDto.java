package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateDraftDto(
        @Schema(description = "Контент для поста", example = "мой первый пост")
        @NotBlank(message = "the content is irrelevant or an empty string ")
        @Size(max = 8192, message = "The message is too long")
        String content
) {
}
