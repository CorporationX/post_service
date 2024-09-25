package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostViewEvent;
import faang.school.postservice.service.FeedService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewConsumer extends AbstractConsumer<PostViewEvent> {

    public KafkaPostViewConsumer(FeedService feedService) {
        super(feedService);
    }

    @KafkaListener(topics = "${spring.kafka.topic.post-views}",
            groupId = "${spring.kafka.consumer.group-id.post-group}")
    public void listen(PostViewEvent event, Acknowledgment ack) {
        handle(event, ack, () -> feedService.addViewToPost(event.getPostId()));
    }
}
