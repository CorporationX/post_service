package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.PostEventDto;
import faang.school.postservice.dto.redis.PostIdDto;
import faang.school.postservice.service.redis.RedisFeedCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener
@RequiredArgsConstructor
public class PostKafkaListener extends AbstractKafkaListener<PostEventDto> {
    private final RedisFeedCacheService redisFeedCacheService;

    @KafkaListener(topics = "${kafka.topics.post.name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, Object> message) {
        consume(message, PostEventDto.class, (this::handlePostEvent));
    }

    private void handlePostEvent(PostEventDto postEventDto, KafkaKey kafkaKey) {
        PostIdDto postIdDto = PostIdDto.builder()
                .postId(postEventDto.getPostId())
                .publishedAt(postEventDto.getPublishedAt())
                .build();

        postEventDto.getAuthorSubscriberIds().forEach(
                (userId) -> {
                    if (kafkaKey == KafkaKey.SAVE) {
                        redisFeedCacheService.savePostToFeed(userId, postIdDto);
                    }
                    if (kafkaKey == KafkaKey.DELETE) {
                        redisFeedCacheService.removePostFromFeed(userId, postIdDto);
                    }
                }
        );
    }
}
