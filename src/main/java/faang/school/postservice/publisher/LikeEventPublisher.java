package faang.school.postservice.publisher;

import faang.school.postservice.events.LikeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.likes}")
    private String likesTopic;

    public void sendMessage(LikeEvent msg) {
        kafkaTemplate.send(likesTopic, msg);
    }
}
