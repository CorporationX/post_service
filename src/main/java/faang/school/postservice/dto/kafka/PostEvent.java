package faang.school.postservice.dto.kafka;

import java.util.List;

public record PostEvent(
        Long postId,
        List<Long> followerIds
) {
}
