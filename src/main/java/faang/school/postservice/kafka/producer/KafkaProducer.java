package faang.school.postservice.kafka.producer;

import faang.school.postservice.properties.KafkaSettingsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaSettingsProperties kafkaSettingsProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object message) {
        String postTopic = kafkaSettingsProperties.getPostTopic();
        kafkaTemplate.send(postTopic, message);
        log.info("Message sent to topic: {}, message: {}", postTopic, message);
    }
}
