package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostLikeEvent;
import faang.school.postservice.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaLikeConsumer extends AbstractConsumer<PostLikeEvent> {

    public KafkaLikeConsumer(FeedService feedService) {
        super(feedService);
    }

    @KafkaListener(topics = "${spring.kafka.topic.like-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostLikeEvent event, Acknowledgment ack) {
         handle(event, ack, () -> feedService.addLikeToPost(event.getPostId()));
    }
}
