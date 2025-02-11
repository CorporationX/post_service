package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.kafka.config.topic-name}")
    private String topicName;

    public void publishLikeEvent(LikeEvent event) {

         log.info("Publishing event like postId{} to Kafka:",event.getPostId() );
        kafkaTemplate.send(topicName, event);
    }
}
