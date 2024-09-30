package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.RedisPostCacheService;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {
    private final RedisPostCacheService postCacheService;
    private final RedisPostCacheService redisPostCacheService;
    private final PostRepository postRepository;
    private final PostMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic-name.comments:comments}")
    void listener(CommentEvent event, Acknowledgment acknowledgment){
        try {
            addComment(event.postId(), event.commentId());
            acknowledgment.acknowledge();
            log.info("Comment with id:{} is successfully added to post.", event.commentId());
        } catch (Exception e) {
            log.error("Comment with id:{} is not added to post.", event.commentId());
            throw e;
        }
    }

    private void addComment(Long postId, Long commentId){
        if (postCacheService.existsById(postId)){
            redisPostCacheService.addCommentToPost(postId, commentId);
        } else {
            var postCache = postRepository.findById(postId)
                    .map(mapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));
            postCacheService.savePostCache(postCache);
        }
    }
}
