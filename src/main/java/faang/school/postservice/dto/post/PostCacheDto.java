package faang.school.postservice.dto.post;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record PostCacheDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        List<Long> likeIds,
        List<Long> commentIds,
        List<Long> albumIds,
        Long adId,
        List<Long> resourceIds,
        Boolean published,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        Boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
