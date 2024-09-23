package faang.school.postservice.producer.post;

import faang.school.postservice.event.post.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostProducer {

    @Value("${kafka.posts-topic.name}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostEvent(PostEvent postEvent) {
        kafkaTemplate.send(topicName, postEvent);
        log.info("send message: {}", postEvent.toString());
    }
}

