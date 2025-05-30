package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, LikePostEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.likes.name}")
    private String likeTopicName;

    public void sendMessage(LikePostEvent event) {
        kafkaTemplate.send(likeTopicName, event);
    }
}
