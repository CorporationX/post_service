package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostEventProducer {

    @Value("${spring.kafka.topics.post-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaPostEventDto> kafkaTemplate;

    public void sendMessage(long postId, List<Long> subscriberIds) {
        kafkaTemplate.send(topic, new KafkaPostEventDto(postId, subscriberIds));
    }
}