package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Evgenii Malkov
 */
@Component
public class KafkaCommentProducer extends AbstractKafkaProducer {

    @Value("${spring.kafka.producer.topics.comments}")
    private String topic;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendMessage(CommentDto commentDto) {
        String key = UUID.randomUUID().toString();
        super.sendMessage(topic, key, commentDto);
    }
}
