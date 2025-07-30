package faang.school.postservice.dto.kafka;

import java.util.List;

public record KafkaPostEventDto(
        long postId,
        List<Long> subscriberIds
) {
}
