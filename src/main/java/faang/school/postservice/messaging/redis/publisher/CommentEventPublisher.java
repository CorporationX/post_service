package faang.school.postservice.messaging.redis.publisher;

import faang.school.postservice.model.Comment;
import faang.school.postservice.messaging.redis.events.CommentEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventPublisher {

    @Setter
    @Value("${spring.data.redis.channels.comment_events_channel.name}")
    private String commentEventsChannel;

    private final RedisMessagePublisher redisMessagePublisher;

    @Autowired
    public CommentEventPublisher(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    public void publishCommentEvent(Comment comment) {
        CommentEvent commentEvent = CommentEvent.builder()
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .commentText(comment.getContent())
                .build();

        redisMessagePublisher.publish(commentEventsChannel, commentEvent);
    }
}
