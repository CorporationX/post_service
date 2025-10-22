package faang.school.postservice.dto.post;

import faang.school.postservice.model.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostDto(
     @Schema(description = "Контент для поста", example = "мой первый пост")
     String content,
     @Schema(description = "ID автора поста", example = "123")
     Long authorId,
     @Schema(description = "PUBLISHED", example = "PUBLISHED- опубликовано, DRAFT - черновик"
             + "DELETED - удален")
     PostStatus postStatus
) {
}
