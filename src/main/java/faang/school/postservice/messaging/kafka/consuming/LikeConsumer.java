package faang.school.postservice.messaging.kafka.consuming;

import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeConsumer {

    private final LikeService likeService;

    @KafkaListener(topics = "${spring.kafka.channels.like_event_channel.name}", groupId = "${spring.kafka.consumer.group}")
    public void listen(LikeEvent likeEvent, Acknowledgment acknowledgment) {
        log.info("event from kafka: " + likeEvent);
        likeService.redisLikeIncrement(likeEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
