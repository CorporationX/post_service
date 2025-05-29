package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentRedisEvent(
        Long id,
        Long postId,
        Long authorId,
        String content,
        LocalDateTime createdAt
) {}
