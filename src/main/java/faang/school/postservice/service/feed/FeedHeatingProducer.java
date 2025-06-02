package faang.school.postservice.service.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedHeatingProducer extends AbstractKafkaProducer {
    private final String topic;

    public FeedHeatingProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                               @Value("${spring.kafka.producer.topics.feed-heating}") String topic) {
        super(kafkaTemplate, objectMapper);
        this.topic = topic;
    }

    public void sendHeatingTask(Long userId) {
        send(topic, userId);
        log.debug("Sent heating task for user: {}", userId);
    }
}
