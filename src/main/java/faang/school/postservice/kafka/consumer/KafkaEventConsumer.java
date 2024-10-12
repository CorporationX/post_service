package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.model.CommentEvent;
import faang.school.postservice.kafka.model.LikeEvent;
import faang.school.postservice.kafka.model.ViewEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {

    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic-name.likes}")
    public void listener(LikeEvent likeEvent, Acknowledgment ack) {
        log.info("Received message like [{}]", likeEvent);
        postCacheService.incrementLikes(likeEvent.postId());
        ack.acknowledge();
        log.info("Successfully received like");
    }

    @KafkaListener(topics = "${spring.data.kafka.topic-name.views}")
    public void listener(ViewEvent viewEvent, Acknowledgment ack) {
        log.info("Received message view [{}]", viewEvent);
        postCacheService.incrementView(viewEvent.postId());
        ack.acknowledge();
        log.info("Successfully received view");
    }

    @KafkaListener(topics = "${spring.data.kafka.topic-name.comments}")
    public void listener(CommentEvent commentEvent) {
        log.info("Received message [{}]", commentEvent);
        postCacheService.addComments(commentEvent);
        log.info("Message sent successfully");
    }
}
