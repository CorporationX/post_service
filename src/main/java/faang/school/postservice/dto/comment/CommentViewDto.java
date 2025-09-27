package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static faang.school.postservice.config.constants.CommentConstants.COMMENT_MAX_SIZE;

public record CommentViewDto(
        Long id,
        @Size(max = COMMENT_MAX_SIZE,
                message = "Содержимое комментария не может быть больше " + COMMENT_MAX_SIZE + " символов")
        String content,
        @NotNull
        Long authorId,
        @NotNull
        Long postId,
        String largeImageFileKey,
        String smallImageFileKey
) {

}
