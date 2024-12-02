package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostLikeEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostLikeProducer extends AbstractKafkaEventProducer<PostLikeEventKafka> {

    public KafkaPostLikeProducer(KafkaTemplate<String, Object> multiTypeKafkaTemplate,
                                 @Value("${spring.kafka.topics.like}") String kafkaTopic) {
        super(multiTypeKafkaTemplate, kafkaTopic);
    }
}
