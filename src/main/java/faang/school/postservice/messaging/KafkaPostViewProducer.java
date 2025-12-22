package faang.school.postservice.messaging;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    @Value("${spring.kafka.topic.name}")
    private String topic;

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    public void sendPostView(Long postId, Long viewerId) {
        PostViewEvent event = new PostViewEvent(
                postId,
                viewerId,
                Instant.now()
        );

        kafkaTemplate.send(
                topic,
                postId.toString(),
                event
        );
    }
}