package faang.school.postservice.consumer;

import faang.school.postservice.event.kafka.KafkaPostLikeEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostLikeConsumer extends KafkaAbstractConsumer<KafkaPostLikeEvent> {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.comments.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(KafkaPostLikeEvent kafkaPostLikeEvent, Acknowledgment acknowledgment) {
        handle(kafkaPostLikeEvent, acknowledgment, () -> {
            long postId = kafkaPostLikeEvent.getPostId();
            feedService.addLikeToPost(postId);
        });
    }
}
