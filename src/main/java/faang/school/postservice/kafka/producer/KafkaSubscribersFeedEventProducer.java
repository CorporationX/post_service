package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSubscribersFeedEventProducer extends AbstractKafkaEventProducer<KafkaSubscribersFeedHeatDto> {

    @Getter
    @Value("${spring.kafka.topics.subscribers-feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaSubscribersFeedHeatDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaSubscribersFeedHeatDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
