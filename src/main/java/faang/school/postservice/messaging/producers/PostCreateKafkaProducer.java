package faang.school.postservice.messaging.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostPublishedDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostCreateKafkaProducer extends AbstractKafkaEventProducer<PostPublishedDto>{

    public PostCreateKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                   @Value("${spring.data.kafka.topics.post-create.name}") String topic,
                                   ObjectMapper mapper) {
        super(kafkaTemplate, topic, mapper);
    }
}
