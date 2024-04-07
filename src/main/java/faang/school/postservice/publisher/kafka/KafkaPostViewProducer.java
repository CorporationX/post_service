package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.PostViewedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaPostViewProducer extends AbstractKafkaEventProducer<PostViewedEvent> {
    @Value("${kafka.topics.post_view.name}")
    private String postViewTopic;

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(long postId, long ownerId, long viewerId) {
        send(postViewTopic, PostViewedEvent.builder()
                .postId(postId)
                .ownerId(ownerId)
                .viewerId(viewerId)
                .build());
    }
}
