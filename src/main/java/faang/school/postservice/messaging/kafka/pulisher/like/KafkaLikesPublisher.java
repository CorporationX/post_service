package faang.school.postservice.messaging.kafka.pulisher.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.messaging.kafka.pulisher.AbstractKafkaPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikesPublisher extends AbstractKafkaPublisher<LikeEvent> {

    public KafkaLikesPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${spring.kafka.topic.likes}") String likesTopicName) {
        super(kafkaTemplate, objectMapper, likesTopicName);
    }

    @Override
    public void publish(LikeEvent event) {
        super.publish(event);
    }
}