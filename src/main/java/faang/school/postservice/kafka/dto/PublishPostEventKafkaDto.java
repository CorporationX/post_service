package faang.school.postservice.kafka.dto;

import java.util.List;

public record PublishPostEventKafkaDto(
        Long postId,
        List<Long> subscribersIds
) {
}
