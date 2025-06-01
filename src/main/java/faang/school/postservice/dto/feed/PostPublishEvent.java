package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostPublishEvent(
        Long postId,
        Long authorId,
        LocalDateTime publishedAt
) {}
