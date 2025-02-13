package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record RedisPostDto(
                           Long id,
                           String content,
                           Long authorId,
                           Long projectId,
                           int likeCount,
                           List<CacheCommentDto> recentComments,
                           LocalDateTime publishedAt,
                           LocalDateTime createdAt,
                           Set<Long> hashtagIds
) {
}
