package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.kafka.event.like.UnlikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UnlikeEventPublisher extends AbstractEventPublisher<UnlikeEvent> {

    private final String unlikeEventsTopic;

    public UnlikeEventPublisher(@Qualifier("KafkaTemplateLikeUnlikeNotification") KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${spring.kafka.topics.unlike-events}") String unlikeEventsTopic) {
        super(kafkaTemplate, unlikeEventsTopic);
        this.unlikeEventsTopic = unlikeEventsTopic;
    }

    public void publishUnlikeEvent(Long postAuthorId, Long likeAuthorId, Long postId, Long likeId) {
        UnlikeEvent unlikeEvent = UnlikeEvent.builder()
                .likeId(likeId)
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
        String key = unlikeEvent.getLikeAuthorId().toString();
        publishEvent(key, unlikeEvent, unlikeEventsTopic);
    }
}
