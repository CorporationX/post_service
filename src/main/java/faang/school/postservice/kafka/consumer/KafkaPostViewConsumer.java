package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.PostViewEventDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final RedisPostCache redisPostCache;
    private final PostService postService;


    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics-names.post-view}", groupId = "spring.kafka.group-id")
    public void handlePostView(PostViewEventDto postViewEvent, Acknowledgment acknowledgment) {
        Long updatedPostViews = postService.incrementPostViews(postViewEvent.getPostId());

        PostForFeedDto post = redisPostCache.findById(postViewEvent.getPostId()).orElseThrow();

        post.setViewsCounter(updatedPostViews);
        redisPostCache.save(post);

        acknowledgment.acknowledge();
    }
}
