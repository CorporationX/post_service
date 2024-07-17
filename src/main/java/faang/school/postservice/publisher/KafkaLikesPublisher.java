package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLikesPublisher implements MessagePublisher<LikeCreatedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.data.kafka.likes-topic}")
    private String likesTopic;

    @Override
    public void publish(LikeCreatedEvent event) {
        kafkaTemplate.send(likesTopic, event.toString());
    }
}
