package faang.school.postservice.publisher;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.post-view}")
    private String postTopic;

    public void sendMessage(PostViewEvent postEvent) {
        kafkaTemplate.send(postTopic, postEvent);
    }
}
