package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;

public record KafkaCommentEventDto(
        long postId,
        String content,
        long authorId,
        LocalDateTime createdAt
) {
}
