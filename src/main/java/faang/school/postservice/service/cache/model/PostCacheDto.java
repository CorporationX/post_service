package faang.school.postservice.service.cache.model;
import java.time.LocalDateTime;
import java.util.List;

public record PostCacheDto(
        Long id,
        Long authorId,
        Long projectId,
        String content,
        List<String> resourceKeys,
        LocalDateTime publishedAt
) {}
