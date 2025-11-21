package faang.school.postservice.publisher.like;

import faang.school.postservice.event.like.UnlikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UnlikeEventPublisher extends AbstractEventPublisher<UnlikeEvent> {

    public UnlikeEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${spring.kafka.topics.unlike-events}") String unlikeEventsTopic) {
        super(kafkaTemplate, unlikeEventsTopic);
    }

    public void publishUnlikeEvent(UnlikeEvent unlikeEvent) {
        String key = unlikeEvent.getPostAuthorId().toString();
        publishEvent(key, unlikeEvent, "UnlikeEvent");
    }

    public void publishUnlikeEvent(Long postAuthorId, Long likeAuthorId, Long postId) {
        UnlikeEvent event = UnlikeEvent.builder()
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .postId(postId)
                .build();
        publishUnlikeEvent(event);
    }
}
