package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaPostViewedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewedEventProducer {
    @Value("${spring.kafka.topics.post-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaPostViewedEventDto> kafkaTemplate;

    public void sendMessage(long postId) {
        kafkaTemplate.send(topic, new KafkaPostViewedEventDto(postId));
    }
}
