package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.service.redis.RedisFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer extends KafkaAbstractConsumer<PostEventDto> {
    private final RedisFeedService redisFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.posts.name:posts-topic}",
            groupId = "${spring.kafka.consumer.group-id.posts:posts-group}",
            containerFactory = "postEventKafkaListenerContainerFactory")
    @Override
    public void consume(PostEventDto event, Acknowledgment acknowledgment) {
        event.getPostAuthorFollowersIds().forEach(followerId -> {
            redisFeedService.removeFirstPostIdIfNecessary(followerId);
            redisFeedService.addTtlForUserPostsIdsListIfNecessary(followerId);
            redisFeedService.addPostId(followerId, event.getPostCreatedAt(), event.getPostId());
        });
        acknowledgment.acknowledge();
    }
}
