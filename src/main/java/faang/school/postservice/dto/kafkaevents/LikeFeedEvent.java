package faang.school.postservice.dto.kafkaevents;

import lombok.Builder;

@Builder
public record LikeFeedEvent(
        Long id,
        Long authorId,
        Long postId
) {
}
