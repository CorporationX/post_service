package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Комментарий к посту")
public record CommentDto(
        @Schema(description = "Уникальный идентификатор комментария")
        Long id,

        @Schema(description = "Текст комментария")
        String content,

        @Schema(description = "ID автора комментария")
        Long authorId,

        @Schema(description = "ID поста, к которому относится комментарий")
        Long postId,

        @Schema(description = "Дата и время создания комментария")
        LocalDateTime createdAt
) {
}
