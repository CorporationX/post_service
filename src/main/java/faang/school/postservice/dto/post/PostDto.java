package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostDto(
     @Schema(description = "Контент для поста", example = "мой первый пост")
     String content,
     @Schema(description = "ID автора поста", example = "123")
     Long authorId
) {
}
