package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.CommentPublishedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaCommentProducer extends AbstractKafkaEventProducer<CommentPublishedEvent>{
    @Value("${kafka.topics.comment.name}")
    private String commentTopic;
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish (long postId, long commentId, long commentOwnerId) {
        send(commentTopic, CommentPublishedEvent.builder()
                .postId(postId)
                .commentId(commentId)
                .commentOwnerId(commentOwnerId)
                .build());
    }
}
