package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.redis.cache.RedisPostCache;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final RedisPostCache redisPostCache;
    private final CommentMapper commentMapper;
    @Value("${spring.data.redis.post-cache.max-comments-amount}")
    private int maxPostsAmount;


    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics-names.comment}", groupId = "spring.kafka.group-id")
    public void handleNewComment(CommentEventDto commentEventDto, Acknowledgment acknowledgment) {
        PostForFeedDto post = redisPostCache.findById(commentEventDto.getPostId()).orElseThrow();
        post.handleNewComment(commentMapper.toDto(commentEventDto), maxPostsAmount);
        redisPostCache.save(post);

        acknowledgment.acknowledge();
    }
}
