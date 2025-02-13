package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record CacheCommentDto(long id,
                              Long authorId,
                              int likeCount,
                              String content,
                              LocalDateTime createdAt
) {
}
