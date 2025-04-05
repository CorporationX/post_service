package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FeedItemCommentDto(
        Long id,
        Long postId,
        String content,
        Long authorId,
        LocalDateTime createdAt
) {  }
