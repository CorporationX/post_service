package faang.school.postservice.producer;

import faang.school.postservice.events.PostPublishedKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${news-feed.posts.topic}")
    private String topic;

    public void sendEvent(PostPublishedKafkaEvent event) {
        kafkaTemplate.send(topic, event);
        log.trace("Post ID {} is published to kafka. User ids count - {}", event.getPostId(),
                event.getAuthorFollowerIds().size());
    }
}
