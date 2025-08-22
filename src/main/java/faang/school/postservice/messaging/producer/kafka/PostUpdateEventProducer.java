package faang.school.postservice.messaging.producer.kafka;

import faang.school.postservice.messaging.dto.PostUpdatedEvent;
import faang.school.postservice.messaging.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka-продюсер для отправки событий об обновлении поста.
 * <p>
 * Использует {@link KafkaTemplate} для сериализации и публикации {@link PostUpdatedEvent}
 * в топик, заданный в настройках {@code kafka.topics.update-post}.
 * </p>
 *
 * @author Myrza
 * @since 06.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateEventProducer implements EventProducer<PostUpdatedEvent> {
    @Value("${kafka.topics.update-post}")
    private String updateTopic;
    private final KafkaTemplate<String, PostUpdatedEvent> kafkaTemplate;

    @Override
    public void send(PostUpdatedEvent event) {
        log.info("send post update event to topic: {}\npostId:{}", updateTopic, event.oldPost().id());
        kafkaTemplate.send(updateTopic, event);
    }
}
