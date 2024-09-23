package faang.school.postservice.model.kafka;

public record KafkaLikeEvent(long postId, long likeAuthorId) {
}
