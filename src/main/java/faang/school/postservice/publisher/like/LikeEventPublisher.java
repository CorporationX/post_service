package faang.school.postservice.publisher.like;

import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    public LikeEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${spring.kafka.topics.like-events}") String likeEventsTopic) {
        super(kafkaTemplate, likeEventsTopic);
    }

    public void publishLikeEvent(LikeEvent likeEvent) {
        String key = likeEvent.getPostAuthorId().toString();
        publishEvent(key, likeEvent, "LikeEvent");
    }

    public void publishLikeEvent(Long postAuthorId, Long likeAuthorId, Long postId) {
        LikeEvent event = LikeEvent.builder()
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .postId(postId)
                .build();
        publishLikeEvent(event);
    }
}