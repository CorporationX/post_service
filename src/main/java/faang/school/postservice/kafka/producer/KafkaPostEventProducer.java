package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostEventProducer extends AbstractKafkaEventProducer<KafkaPostEventDto> {

    @Getter
    @Value("${spring.kafka.topics.post-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaPostEventDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaPostEventDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}