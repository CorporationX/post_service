package faang.school.postservice.dto.comment;

import java.time.Instant;

public record FeedCommentDto(
        Long id,
        Long authorId,
        String content,
        Instant createdAt
) {}
