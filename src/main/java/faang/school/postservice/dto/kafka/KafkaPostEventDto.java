package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;

public record KafkaPostEventDto(
        long id,
        String content,
        Long authorId,
        Long likeCount,
        Long commentCount,
        LocalDateTime createdAt
) {
}
