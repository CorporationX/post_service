package faang.school.postservice.dto.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostEventDto(
        Long postId,
        Long authorId,
        Long projectId,
        List<Long> followerIds,
        LocalDateTime publishedAt
) {
}
