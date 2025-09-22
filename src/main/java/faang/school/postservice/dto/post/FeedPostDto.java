package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.feed.FeedAuthorDto;

import java.time.Instant;

public record FeedPostDto(
        Long id,
        String content,
        Instant publishedAt,
        FeedAuthorDto author
) {
}
