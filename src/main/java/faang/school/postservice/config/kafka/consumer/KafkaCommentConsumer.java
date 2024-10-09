package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.redis.CommentRedisService;
import faang.school.postservice.service.redis.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {
    private final CommentRedisService commentRedisService;
    private final PostRedisService postRedisService;
    private final CommentMapper commentMapper;
    private String zsetKey;
    private String postKey;

    @KafkaListener(topics = "comment_theme", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentDto event,
                       Acknowledgment acknowledgment) {
        log.info(event.toString());
        zsetKey = "comments:" + event.getPostId();
        postKey = "post:" + event.getPostId();
        commentRedisService.saveComment(commentMapper.toRedisEntity(event), zsetKey);
        postRedisService.addComment(postKey, zsetKey);
        acknowledgment.acknowledge();
    }
}
