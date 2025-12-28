package faang.school.postservice.dto.kafka;

import java.util.List;

public record PostForFeedDto(
        Long postAuthorId,
        List<Long> subscriberIds,
        Long postId
) {
}
