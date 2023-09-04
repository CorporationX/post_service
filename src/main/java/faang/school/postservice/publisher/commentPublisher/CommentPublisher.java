package faang.school.postservice.publisher.commentPublisher;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.service.redisPublisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentPublisher {
    private final MessagePublisher messagePublisher;

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private final String eventChannel;

    public void sendEventNewComment(CommentEvent event){
        messagePublisher.publish(eventChannel, event);
    }
}
