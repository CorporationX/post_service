package faang.school.postservice.publisher;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.FeedUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedUpdateEventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.post}")
    private String newPostTopic;

    public void sendMessage(FeedUpdateEvent feedUpdateEvent) {
        kafkaTemplate.send(newPostTopic, feedUpdateEvent);
    }
}
