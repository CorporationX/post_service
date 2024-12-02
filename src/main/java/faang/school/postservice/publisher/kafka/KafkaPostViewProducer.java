package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostViewEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractKafkaEventProducer<PostViewEventKafka> {

    public KafkaPostViewProducer(KafkaTemplate<String, Object> multiTypeKafkaTemplate,
                             @Value("${spring.kafka.topics.post-view}") String kafkaTopic) {
        super(multiTypeKafkaTemplate, kafkaTopic);
    }
}
