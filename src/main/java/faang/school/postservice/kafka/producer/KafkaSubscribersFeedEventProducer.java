package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSubscribersFeedEventProducer implements KafkaEventProducer<KafkaSubscribersFeedHeatDto> {

    @Value("${spring.kafka.topics.subscribers-feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaSubscribersFeedHeatDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaSubscribersFeedHeatDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }
}
