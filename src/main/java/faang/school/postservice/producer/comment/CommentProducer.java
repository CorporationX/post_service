package faang.school.postservice.producer.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.producer.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentProducer extends AbstractProducer<CommentEvent> {

    public CommentProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.kafka.topic.comment.name}") String topicName
    ) {
        super(kafkaTemplate, topicName, objectMapper);
    }

}
