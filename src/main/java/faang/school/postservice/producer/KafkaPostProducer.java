package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.new-post}")
    private String topicName;

    public void sendMessage(NewPostEvent event) {
        kafkaTemplate.send(topicName, event);
    }
}
