package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.redis.CommentRedisService;
import faang.school.postservice.service.redis.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostRedisService postRedisService;
    private final CommentRedisService commentRedisService;

    @KafkaListener(topics = "like_theme", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(LikeDto event,
                       Acknowledgment acknowledgment) {
        if (event.getPostId() != null){
            postRedisService.addLike("post:" + event.getPostId());
        } else {
            commentRedisService.addLike("com:" + event.getCommentId());
        }
        log.info(event.toString());
        acknowledgment.acknowledge();
    }
}
