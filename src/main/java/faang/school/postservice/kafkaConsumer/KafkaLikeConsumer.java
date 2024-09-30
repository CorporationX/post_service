package faang.school.postservice.kafkaConsumer;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class KafkaLikeConsumer extends AbstractKafkaConsumer<LikeEvent> {

    public KafkaLikeConsumer(FeedService feedService) {
        super(feedService);
    }

    @KafkaListener(topics = "${spring.data.kafka.topic.like}", groupId = "${spring.data.kafka.consumer.group_id}")
    public void listen(LikeEvent event, Acknowledgment ack) {
        log.info("Received like event: {}", event);
        handle(event, ack, () -> feedService.addLikeToPost(event.getPostId()));
    }
}
