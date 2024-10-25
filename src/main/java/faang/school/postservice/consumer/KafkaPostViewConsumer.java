package faang.school.postservice.consumer;

import faang.school.postservice.event.kafka.KafkaPostViewEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer extends KafkaAbstractConsumer<KafkaPostViewEvent> {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.comments.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(KafkaPostViewEvent kafkaPostViewEvent, Acknowledgment acknowledgment) {
        handle(kafkaPostViewEvent, acknowledgment, () -> {
            long postId = kafkaPostViewEvent.getPostId();
            feedService.addViewToPost(postId);
        });
    }
}
