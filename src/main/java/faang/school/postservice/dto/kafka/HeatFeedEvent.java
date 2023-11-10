package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.PostPair;
import lombok.Builder;

@Builder
public record HeatFeedEvent(
        Long userId,
        PostPair postPair
) {
}
