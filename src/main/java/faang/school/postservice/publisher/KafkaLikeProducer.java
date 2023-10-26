package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, KafkaLikeEvent> kafkaTemplate;
    @Value("${spring.data.kafka.topics.like}")
    private String likesTopic;

    public void publishLikeEvent(KafkaLikeEvent kafkaLikeEvent) {
        kafkaTemplate.send(likesTopic, kafkaLikeEvent);
    }
}
