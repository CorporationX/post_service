package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FeedPostDto(
		Long id,
		String content,
		Long authorId,
		String username,
		LocalDateTime publishedAt
) {
}