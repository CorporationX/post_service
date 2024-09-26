package faang.school.postservice.model.kafka;

public record KafkaCommentEvent(long postId, long commentAuthorId) {

}
