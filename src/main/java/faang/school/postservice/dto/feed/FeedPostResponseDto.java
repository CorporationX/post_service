package faang.school.postservice.dto.feed;

import java.time.LocalDateTime;

public record FeedPostResponseDto(
        Long id,
        String content,
        boolean published,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long projectId,
        AuthorDto author
) {}

