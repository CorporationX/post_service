package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafkaevents.LikeFeedEvent;
import faang.school.postservice.exception.KafkaEventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeEventPublisher {
    private final KafkaTemplate<String, LikeFeedEvent> kafkaTemplate;

    @Value("${spring.data.kafka.topic.likes}")
    private String postLikeTopic;

    public void publish(LikeFeedEvent event) {
        try {
            kafkaTemplate.send(postLikeTopic, event);
            log.info("Ивент {} отправлен. {} поставил лайк на пост {}", event.id(), event.authorId(), event.postId());
        } catch (Exception e) {
            log.error("Ошибка отправки ивента {}", event.id());
            throw new KafkaEventPublishException("Не удалось отправить ивент", e);
        }
    }
}

