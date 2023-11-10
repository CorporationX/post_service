package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.HeatFeedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeatPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.data.kafka.topics.heater.name}")
    private String heaterTopic;

    public void publish(HeatFeedEvent event) {
        kafkaTemplate.send(heaterTopic, event);
        log.info("Heat event was published to Kafka with User ID: {}, Post ID: {}", event.userId(), event.postPair().postId());
    }
}
