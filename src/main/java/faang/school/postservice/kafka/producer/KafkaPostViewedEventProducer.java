package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaPostViewedEventDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewedEventProducer implements KafkaEventProducer<KafkaPostViewedEventDto> {

    @Value("${spring.kafka.topics.post-viewed-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaPostViewedEventDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaPostViewedEventDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }
}
