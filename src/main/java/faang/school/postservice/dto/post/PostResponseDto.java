package faang.school.postservice.dto.post;

import faang.school.postservice.dto.feed.FeedItemCommentDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Builder
public record PostResponseDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        long postLikesCounter,
        long postViewsCounter,
        boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime publishedAt,
        LinkedHashSet<FeedItemCommentDto> comments) {
}
