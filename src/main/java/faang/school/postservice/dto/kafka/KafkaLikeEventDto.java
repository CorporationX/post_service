package faang.school.postservice.dto.kafka;

public record KafkaLikeEventDto(
        long postId,
        long likeAuthorId
) {
}
