package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.PostViewedEventDto;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.service.redis.RedisPostCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.function.Consumer;

@KafkaListener
@RequiredArgsConstructor
public class PostViewKafkaListener extends AbstractKafkaListener<PostViewedEventDto> {
    private final RedisPostCacheService redisPostCacheService;

    @KafkaListener(topics = "${kafka.topics.post_view.name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, Object> message) {
        PostViewedEventDto event = getEvent(message, PostViewedEventDto.class);

        consume(message, PostViewedEventDto.class, this::handlePostViewedEvent);
    }

    private void handlePostViewedEvent(PostViewedEventDto postViewedEventDto, KafkaKey kafkaKey) {
        Consumer<RedisPost> consumer = null;

        if (kafkaKey == KafkaKey.SAVE) {
            consumer = redisPost -> redisPost.getLikedUserIds().add(postViewedEventDto.getViewerId());
        }
        if (kafkaKey == KafkaKey.DELETE) {
            consumer = redisPost -> redisPost.getLikedUserIds().remove(postViewedEventDto.getViewerId());
        }


        if (consumer != null) {
            redisPostCacheService.update(postViewedEventDto.getPostId(), consumer);
        }
    }
}
