package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.PostViewedEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewKafkaProducer extends AbstractKafkaProducer<PostViewedEventDto> {
    @Value("${kafka.topics.post_view.name}")
    private String postViewTopic;

    public PostViewKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(long postId, long ownerId, long viewerId) {
        send(postViewTopic, PostViewedEventDto.builder()
                .postId(postId)
                .ownerId(ownerId)
                .viewerId(viewerId)
                .build());
    }
}
