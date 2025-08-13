package faang.school.postservice.newsfeed;

import faang.school.postservice.newsfeed.events.PostPublishEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private static final String POST_TOPIC = "posts";

    @KafkaListener(topics = POST_TOPIC, groupId = "posts_consumers", concurrency = "3")
    public void processPostEvents(PostPublishEvent event) {

    }
}
