package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventProducer extends AbstractKafkaEventProducer<KafkaFeedHeatDto> {

    @Getter
    @Value("${spring.kafka.topics.feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaFeedHeatDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaFeedHeatDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
