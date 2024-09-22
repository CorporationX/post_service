package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostCommentEvent;
import faang.school.postservice.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaCommentConsumer extends AbstractConsumer<PostCommentEvent> {

    public KafkaCommentConsumer(FeedService feedService) {
        super(feedService);
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.comment-post}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostCommentEvent event, Acknowledgment ack) {
        log.info("New comment event received: {}", event);
        feedService.addCommentToPost(event.getPostId(), event.getComment());
        ack.acknowledge();
    }
}
