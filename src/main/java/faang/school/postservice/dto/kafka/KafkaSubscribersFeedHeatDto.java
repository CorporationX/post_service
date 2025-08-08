package faang.school.postservice.dto.kafka;

import java.util.List;

public record KafkaSubscribersFeedHeatDto(
        Long userId,
        List<Long> followerIds
) {
}
