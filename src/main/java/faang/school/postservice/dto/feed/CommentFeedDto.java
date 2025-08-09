package faang.school.postservice.dto.feed;

import java.time.LocalDateTime;

public record CommentFeedDto(
        long postId,
        String content,
        long authorId,
        LocalDateTime createdAt
) {
}
