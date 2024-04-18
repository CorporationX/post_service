package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.PostEventDto;
import faang.school.postservice.dto.redis.PostFeedDto;
import faang.school.postservice.service.redis.RedisFeedCacheService;
import faang.school.postservice.service.redis.RedisPostCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDateTime;
import java.util.Set;

@KafkaListener
@RequiredArgsConstructor
public class PostKafkaListener extends AbstractKafkaListener<PostEventDto> {

    private final RedisPostCacheService redisPostCacheService;
    private final RedisFeedCacheService redisFeedCacheService;

    @KafkaListener(topics = "${kafka.topics.post.name}", groupId = "${kafka.consumer.group-id}")
    public void consumeee(ConsumerRecord<String, Object> message) {
        consume(message, PostEventDto.class, ((postEventDto, kafkaKey) ->));
    }

    private void handlePostEvent(PostEventDto postEventDto, KafkaKey kafkaKey) {
        Set<Integer> authorSubscriberIds = postEventDto.getAuthorSubscriberIds();
        Long postId = postEventDto.getPostId();
        LocalDateTime publishedAt = postEventDto.getPublishedAt();
        if (kafkaKey.equals(KafkaKey.CREATE)) {
            add(authorSubscriberIds, postId, publishedAt);
        } else {

        }
    }

    private void add (Set<Integer> authorSubscriberIds, Long postId, LocalDateTime publishedAt) {
        authorSubscriberIds.forEach(
                userId -> redisFeedCacheService.addPostToFeed(userId, new PostFeedDto(postId, publishedAt))
        );
    }

    private void remove (Set<Integer> authorSubscriberIds, Long postId, LocalDateTime publishedAt) {
        authorSubscriberIds.forEach(
                userId -> redisFeedCacheService.removePostFromFeed(userId, new PostFeedDto(postId, publishedAt))
        );
    }
}
