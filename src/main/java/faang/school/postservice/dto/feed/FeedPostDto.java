package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.author.AuthorDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FeedPostDto(
        Long id,
        String content,
        AuthorDto author,
        LocalDateTime publishedAt,
        Integer likesCount,
        Integer commentsCount
) {
}
