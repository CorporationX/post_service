package faang.school.postservice.messaging.consumer.kafka;

import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.messaging.consumer.EventConsumer;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer для обработки событий удаления поста.
 * <p>
 * Подписан на топик, указанный в {@code kafka.topics.delete-post}.
 * Получает {@link PostViewDto} и передаёт его в {@link HashtagService} для удаления
 * связей между постом и хэштегами.
 * </p>
 *
 * @author Myrza
 * @since 13.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteEventConsumer implements EventConsumer<PostViewDto> {
    private final HashtagService service;

    @Override
    @KafkaListener(topics = "${kafka.topics.delete-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostViewDto post) {
        log.info("consume post create event, post.id: {}", post.id());
        service.delete(post);
    }
}
