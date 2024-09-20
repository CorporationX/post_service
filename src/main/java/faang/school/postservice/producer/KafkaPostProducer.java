package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.new-post}")
    private String topicName;

    public void sendMessage(NewPostEvent event) {
        log.info("Sending event to topic: {}", topicName);
        kafkaTemplate.send(topicName, event);
    }
}
