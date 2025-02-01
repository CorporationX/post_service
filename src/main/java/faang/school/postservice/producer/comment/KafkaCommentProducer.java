package faang.school.postservice.producer.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.producer.AbstractKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentEvent> {

    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${spring.data.kafka.topics.comments.name}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }

}
