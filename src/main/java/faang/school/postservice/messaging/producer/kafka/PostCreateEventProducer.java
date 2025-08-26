package faang.school.postservice.messaging.producer.kafka;

import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.messaging.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka-продюсер для отправки событий о создании поста.
 * <p>
 * Использует {@link KafkaTemplate} для сериализации и публикации {@link PostViewDto}
 * в топик, заданный в настройках {@code kafka.topics.create-post}.
 * </p>
 *
 * @author Myrza
 * @since 05.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCreateEventProducer implements EventProducer<PostViewDto> {
    @Value("${kafka.topics.create-post}")
    private String createTopic;
    private final KafkaTemplate<String, PostViewDto> kafkaTemplate;

    @Override
    public void send(PostViewDto event) {
        log.info("send post create event to topic: {}\npostId:{}", createTopic, event.id());
        kafkaTemplate.send(createTopic, event);
    }
}
