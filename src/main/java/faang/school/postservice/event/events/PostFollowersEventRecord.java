package faang.school.postservice.event.events;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostFollowersEventRecord(
        Long authorId,
        Long postId,
        List<Long> followersIds,
        LocalDateTime publishedAt
) {
}
