package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements KafkaEventPublisher<LikeEvent> {

    private final KafkaTemplate<String,LikeEvent> kafkaTemplate;

    @Value("${spring.data.kafka.topic.likes.name}")
    private String topic;

    @Override
    public void publish(LikeEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Published event {}", event);
    }
}
