package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.CommentPublishedEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaEventProducer<CommentPublishedEventDto>{
    @Value("${kafka.topics.comment.name}")
    private String commentTopic;
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish (long postId, long commentId, long commentOwnerId) {
        send(commentTopic, CommentPublishedEventDto.builder()
                .postId(postId)
                .commentId(commentId)
                .authorId(commentOwnerId)
                .build());
    }
}