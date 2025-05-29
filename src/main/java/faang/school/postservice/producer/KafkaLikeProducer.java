package faang.school.postservice.producer;

import faang.school.postservice.events.LikePublishKafkaEvent;
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

    @Value("${news-feed.likes.topic}")
    private String topic;

    public void sendEvent(LikePublishKafkaEvent event) {
        kafkaTemplate.send(topic, event);
        log.trace("Like ID {} is published for post ID {} to kafka. Like event type {}", event.getLikeId(),
                event.getPostId(), event.getLikeEventType());
    }
}
