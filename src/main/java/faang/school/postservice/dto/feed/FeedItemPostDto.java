package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Builder
public record FeedItemPostDto(
        Long id,
        String content,
        Long authorId,
        long postLikesCounter,
        long postViewsCounter,
        LinkedHashSet<FeedItemCommentDto> comments,
        LocalDateTime publishedAt) {
}
