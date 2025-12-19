package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostProducer {
    private final KafkaTemplate<String, Object> postKafkaTemplate;

    @Value("${kafka.topics.posts.topic_name}")
    private String topicName;

    public void sendToKafka(PostEvent postEvent) {
        postKafkaTemplate.send(topicName, postEvent);
        log.info("Новый postEvent с postId: {} отправлен в Kafka.", postEvent.postId());
    }
}

