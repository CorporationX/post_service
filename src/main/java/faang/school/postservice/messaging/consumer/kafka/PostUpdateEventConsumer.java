package faang.school.postservice.messaging.consumer.kafka;

import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.messaging.consumer.EventConsumer;
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
public class PostUpdateEventConsumer implements EventConsumer<PostViewDto> {
    @Override
    @KafkaListener(topics = "${kafka.topics.update-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostViewDto event) {
        log.info("consume post create event\nbody: {}", event);
        // TODO: implement method
    }
}
