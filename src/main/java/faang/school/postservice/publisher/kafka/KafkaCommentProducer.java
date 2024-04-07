package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.CommentPublishedEvent;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaCommentProducer extends AbstractKafkaEventProducer<CommentPublishedEvent>{
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish (long postId, long commentId, long commentOwnerId) {
        CommentPublishedEvent.builder()
                .postId(postId)
                .commentId(commentId)
                .commentOwnerId(commentOwnerId)
                .build();
    }
}
