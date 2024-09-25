package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostCommentEvent;
import faang.school.postservice.service.FeedService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentConsumer extends AbstractConsumer<PostCommentEvent> {

    public KafkaCommentConsumer(FeedService feedService) {
        super(feedService);
    }

    @KafkaListener(topics = "${spring.kafka.topic.comment-post}", groupId = "${spring.kafka.consumer.group-id.post-group}")
    public void listen(PostCommentEvent event, Acknowledgment ack) {
        handle(event, ack, () ->
                feedService.addCommentToPost(event.getPostId(), event.getComment()));
    }
}
