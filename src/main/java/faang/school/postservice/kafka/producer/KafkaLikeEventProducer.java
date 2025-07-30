package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaLikeEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeEventProducer {
    @Value("${spring.kafka.topics.like-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaLikeEventDto> kafkaTemplate;

    public void sendMessage(long postId, long likeAuthorId) {
        kafkaTemplate.send(topic, new KafkaLikeEventDto(postId, likeAuthorId));
    }
}
