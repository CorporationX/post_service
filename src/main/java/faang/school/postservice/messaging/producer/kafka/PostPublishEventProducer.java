package faang.school.postservice.messaging.producer.kafka;

import faang.school.postservice.messaging.dto.PostPublishEvent;
import faang.school.postservice.messaging.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostPublishEventProducer implements EventProducer<PostPublishEvent> {
    @Value("${kafka.topics.publish-post}")
    private String publishTopic;
    private final KafkaTemplate<String, PostPublishEvent> kafkaTemplate;

    @Override
    public void send(PostPublishEvent event) {
        log.info("Отправка эвента публикации поста в топик {}", publishTopic);
        kafkaTemplate.send(publishTopic, event);
    }
}
