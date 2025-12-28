package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.kafka.event.like.LikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    private final String likeEventsTopic;

    public LikeEventPublisher(@Qualifier("KafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${spring.kafka.topics.like-events}") String likeEventsTopic) {
        super(kafkaTemplate, likeEventsTopic);
        this.likeEventsTopic = likeEventsTopic;
    }

    public void publishLikeEvent(Long postAuthorId, Long likeAuthorId, Long postId, Long likeId) {
        LikeEvent likeEvent = LikeEvent.builder()
                .likeId(likeId)
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
        String key = likeEvent.getLikeAuthorId().toString();
        publishEvent(key, likeEvent, likeEventsTopic);
    }
}