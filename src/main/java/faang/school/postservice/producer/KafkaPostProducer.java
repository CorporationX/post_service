package faang.school.postservice.producer;

import faang.school.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaPostProducer {
    @Value("${spring.data.kafka.channels.post-channel}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostEvent(PostEvent postEvent) {
        log.info("Publishing post: {} to channel: {}", postEvent, topicName);
        kafkaTemplate.send(topicName, postEvent);
    }
}
