package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema
public record CommentReadDto(
        @Schema(
                description = "Идентификатор комментария",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        long id,
        @Schema(
                description = "Содержание комментария",
                example = "Крутой пост!",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String content,
        @Schema(
                description = "Идентификатор автора комментария",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        long authorId,
        @Schema(
                description = "Идентификаторы лайков комментария",
                example = "[1, 2]",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        List<Long> likesId,
        @Schema(
                description = "Идентификатор поста, к которому относится комментарий",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        long postId,
        @Schema(
                description = "Идентификатор файлов комментария",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        List<Long> filesId,
        @Schema(
                description = "Дата создания комментария",
                example = "2021-07-01T12:00:00",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        LocalDateTime updatedAt
) {
}
