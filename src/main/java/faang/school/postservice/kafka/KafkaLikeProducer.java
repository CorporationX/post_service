package faang.school.postservice.kafka;

import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.consumer.topics.like_topic}")
    private String topicName;

    public void publishToKafka(LikeEvent likeEvent) {
        kafkaTemplate.send(topicName, likeEvent);
        log.info("LikeEvent sent to Kafka: {}", likeEvent);
    }
}
