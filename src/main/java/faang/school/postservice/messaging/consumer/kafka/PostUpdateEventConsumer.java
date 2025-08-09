package faang.school.postservice.messaging.consumer.kafka;

import faang.school.postservice.messaging.consumer.EventConsumer;
import faang.school.postservice.messaging.dto.PostUpdatedEvent;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * PostUpdateEventConsume — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 06.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateEventConsumer implements EventConsumer<PostUpdatedEvent> {
    private final HashtagService service;
    @Override
    @KafkaListener(topics = "${kafka.topics.update-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostUpdatedEvent event) {
        log.info("consume post create event\nbody: {}", event);
        service.update(event.oldPost(), event.newPost());
    }
}
