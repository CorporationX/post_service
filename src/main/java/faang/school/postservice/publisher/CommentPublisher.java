package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class CommentPublisher extends AbstractEventPublisher<CommentEvent> {
    // TODO: другой топик
    public CommentPublisher(
            @Value("") String topic,
            KafkaTemplate<String, CommentEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
