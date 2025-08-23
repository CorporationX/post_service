package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaPostViewedEventDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewedEventProducer extends AbstractKafkaEventProducer<KafkaPostViewedEventDto> {

    @Getter
    @Value("${spring.kafka.topics.post-viewed-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaPostViewedEventDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaPostViewedEventDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
