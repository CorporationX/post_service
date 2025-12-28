package faang.school.postservice.messages.kafka.consumer;

import faang.school.postservice.dto.comment.PublishCommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentConsumer {
    private static final String KEY = "post:comment";
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${max-comment}")
    private int maxCommentPost;

    @KafkaListener(containerFactory = "objectContainerFactory", topics = "${spring.kafka.topics.comment}",
            groupId = "${spring.kafka.consumer.group-id.comment}")
    public void commentConsumer(PublishCommentDto commentDto, Acknowledgment ack) {
        log.info("Consume message dto {}", commentDto.authorId());
        String key = KEY + commentDto.postId();
        long score = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(key, commentDto, score);

        Long size = redisTemplate.opsForZSet().size(key);
        log.info("Comment set have size {}", size);
        if (size != null && size > maxCommentPost) {
            log.info("Deleting an old comment");
            long excess = size - maxCommentPost;
            redisTemplate.opsForZSet().removeRange(key, 0, excess - 1);
        }
        ack.acknowledge();
    }
}