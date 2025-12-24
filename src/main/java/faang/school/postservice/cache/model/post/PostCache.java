package faang.school.postservice.cache.model.post;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Builder
@FieldNameConstants
public record PostCache(
        Long id,
        String content,
        Long authorId,
        LocalDateTime publishedAt,
        Integer likesCount,
        Integer commentsCount
) {
}