package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.redis.master.RedisProperties;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.MessagePublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher<Comment> {
    private static final String TOPIC_NAME = "comment_topic";

    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentEventMapper commentEventMapper;
    private final RedisProperties properties;
    private String topic;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publish(Comment comment) {
        CommentEvent commentEvent = commentEventMapper.toEvent(comment);
        redisTemplate.convertAndSend(topic, commentEvent);
        log.info("Message published. Comment (id {}) has been created by user (id {}) for post (id {}). "
                , commentEvent.commentId(), commentEvent.authorId(), commentEvent.postId());
    }
}