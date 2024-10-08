package faang.school.postservice.kafka.events;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostFollowersEvent(
        Long authorId,
        Long postId,
        List<Long> followersIds,
        LocalDateTime publishedAt
) {}