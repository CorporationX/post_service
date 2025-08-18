package faang.school.postservice.dto.feed;

import java.time.LocalDateTime;
import java.util.List;

public record FeedPostDto(
        Long id,
        String content,
        Long projectId,
        List<String> resourceKeys,
        LocalDateTime publishedAt,
        AuthorShortDto author
) {}
