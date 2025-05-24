package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafkaevents.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostEventPublisher {

    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Value("${spring.data.kafka.topic.posts}")
    private String postPublishTopic;

    public void publish(PostEvent event) {
        try {
            kafkaTemplate.send(postPublishTopic, event);
            log.info("Событие о посте {} отправлено {} пользователям", event.postId(), event.followers().size());
        } catch (Exception e) {
            log.error("Не удалось отправить событие о посте {}", event.postId());
            throw new RuntimeException("Не удалось отправить событие", e);
        }

    }
}
