package faang.school.postservice.dto.kafka;

public record KafkaCommentEventDto(
        long postId,
        long userId
) {
}
