package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.PostPair;
import lombok.Builder;

import java.util.List;

@Builder
public record PostEvent(
        PostPair postPair,
        List<Long> followersIds,
        EventAction eventAction
) {
}
