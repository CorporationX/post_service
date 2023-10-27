package faang.school.postservice.messaging.kafka.consuming;

import faang.school.postservice.messaging.kafka.events.PostViewEvent;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final PostService postService;

    @KafkaListener(topics = "${spring.kafka.channels.post_view_event_channel.name}", groupId = "${spring.kafka.consumer.group}")
    public void listen(PostViewEvent event, Acknowledgment acknowledgment) {
        log.info("event from kafka: " + event);
        postService.incrementView(event.getPostId(), event.getViews());
        acknowledgment.acknowledge();
    }
}
