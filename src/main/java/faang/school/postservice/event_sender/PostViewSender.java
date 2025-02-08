package faang.school.postservice.event_sender;

import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.producer.KafkaPostViewProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostViewSender {
    private final KafkaPostViewProducer postViewProducer;

    public void sendEvent(PostViewEvent postViewEvent) {
        postViewProducer.send(postViewEvent);

        log.debug("Event view post {} successfully send to Kafka", postViewEvent.getPostId());
    }
}
