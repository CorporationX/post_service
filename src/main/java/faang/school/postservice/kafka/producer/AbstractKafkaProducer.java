package faang.school.postservice.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
public class AbstractKafkaProducer<T> {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String key, T message) {
        kafkaTemplate.send(topic, key, message);
        log.info("Sent message with key: {}, to topic: {}", key, topic);
    }
}
