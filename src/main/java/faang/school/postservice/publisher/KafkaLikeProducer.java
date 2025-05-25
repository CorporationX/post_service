package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer implements KafkaEventPublisher<LikeEvent> {

    private KafkaTemplate<String,LikeEvent> kafkaTemplate;
    @Value("${spring.data.kafka.topic.likes.name}")
    private String topic;


    @Override
    public void publish(LikeEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
