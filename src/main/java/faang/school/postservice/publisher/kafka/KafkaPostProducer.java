package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractKafkaEventProducer<PostEventKafka>{
    public KafkaPostProducer(KafkaTemplate<String, Object> multiTypeKafkaTemplate,
                             @Value("${spring.kafka.topics.posts}") String kafkaTopic) {
        super(multiTypeKafkaTemplate, kafkaTopic);
    }
}
