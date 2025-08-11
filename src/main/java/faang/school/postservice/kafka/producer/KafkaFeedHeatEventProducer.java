package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventProducer implements KafkaEventProducer<KafkaFeedHeatDto> {

    @Value("${spring.kafka.topics.feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaFeedHeatDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaFeedHeatDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }
}
