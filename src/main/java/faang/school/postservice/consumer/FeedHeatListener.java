package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.FeedHeatEvent;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.exception.RetryableException;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.service.cache.FeedCacheService;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.service.cache.UserCacheService;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeatListener extends KafkaEventListener {
    private final ObjectMapper objectMapper;
    private final FeedCacheService feedCacheService;
    private final PostCacheService postCacheService;
    private final UserCacheService userCacheService;
    private final UserService userService;
    private final CommentService commentService;
    private final FeedProps feedProps;

    @KafkaListener(
            topics = "${spring.kafka.topic.feedHeat}",
            groupId = "feed")
    public void consumeFeedHeat(String data, Acknowledgment ack) {
        try {
            FeedHeatEvent event = objectMapper.readValue(data, FeedHeatEvent.class);
            log.info("Feed heat event received from Kafka: {}", data);
            feedCacheService.addPosts(event.userId(), event.posts());
            event.posts().forEach(post -> {
                UserRedis userRedis = userService.toUserRedis(post.authorId(), post.projectId());
                userCacheService.save(userRedis);
                postCacheService.save(post);
                List<CommentDto> comments = commentService.findNewByPostId(post.id(), feedProps.comment().limit());
                postCacheService.saveComments(post.id(), comments);
            });
            ack.acknowledge();
        } catch (RetryableException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NonRetryableException(e);
        }
    }
}
