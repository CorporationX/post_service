package faang.school.postservice.messaging.consumer.kafka;

import faang.school.postservice.cache.service.PostCacheService;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.messaging.consumer.EventConsumer;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer для обработки событий создания поста.
 * <p>
 * Подписан на топик, указанный в {@code kafka.topics.create-post}.
 * Получает {@link PostViewDto} и передаёт его в {@link HashtagService} для
 * создания связей между постом и хэштегами.
 * </p>
 *
 * @author Myrza
 * @since 05.08.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostCreateEventConsumer implements EventConsumer<PostViewDto> {
    private final HashtagService service;

    @Override
    @KafkaListener(topics = "${kafka.topics.create-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostViewDto post) {
        log.info("consume post create event, post.id: {}", post.id());
        service.create(post);
    }
}
