package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;

public record KafkaCommentDto(
        long postId,
        String content,
        long authorId,
        LocalDateTime createdAt
) {
}