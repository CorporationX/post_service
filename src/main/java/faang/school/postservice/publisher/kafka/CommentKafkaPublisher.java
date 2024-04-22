package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.CommentPublishedEventDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentKafkaPublisher extends AbstractKafkaPublisher<CommentPublishedEventDto> {
    @Value("${kafka.topics.comment.name}")
    private String commentTopic;
    public CommentKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish ( long commentId, long postId, KafkaKey kafkaKey) {
        send(commentTopic, kafkaKey, CommentPublishedEventDto.builder()
                .postId(postId)
                .commentId(commentId)
                .build());
    }
}