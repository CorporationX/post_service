package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaEventProducer<CommentEventKafka> {


    public KafkaCommentProducer(KafkaTemplate<String, Object> multiTypeKafkaTemplate,
                                @Value("${spring.kafka.topics.comment}") String kafkaTopic) {
        super(multiTypeKafkaTemplate, kafkaTopic);
    }
}
