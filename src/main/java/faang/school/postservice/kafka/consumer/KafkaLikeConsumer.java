package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.service.like.PostLikeService;
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
public class KafkaLikeConsumer {
    private final RedisPostCache redisPostCache;
    private final PostLikeService likeService;

    @KafkaListener(topics = "${spring.kafka.topics-names.like}", groupId = "spring.kafka.group-id")
    public void handleNewLike(LikeEventDto likeEventDto, Acknowledgment acknowledgment) {
        handleNewLike(likeEventDto.getPostId(), likeEventDto.getLikeId());

        acknowledgment.acknowledge();
    }

    @Transactional
    public void handleNewLike(long postId, long likeId) {
        PostForFeedDto post = redisPostCache.findById(postId).orElseThrow();
        post.handleNewLike(likeService.getLikeById(likeId));
        redisPostCache.save(post);
    }
}
