package faang.school.postservice.dto.kafka;

import java.util.List;

public record KafkaFeedHeatDto(
        List<Long> users
) {
}
