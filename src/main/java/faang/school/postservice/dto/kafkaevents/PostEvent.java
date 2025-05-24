package faang.school.postservice.dto.kafkaevents;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PostEvent(
        Long postId,
        Long authorId,
        Instant publishedAt,
        List<Long> followers
) {

}
