package faang.school.postservice.producer.post;

import faang.school.postservice.event.post.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostProducer {

    @Value("${kafka.posts-topic.name}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostEvent(PostEvent postEvent) {
        kafkaTemplate.send(topicName, postEvent);
        System.err.println("send message: " + postEvent.toString());
    }
}

