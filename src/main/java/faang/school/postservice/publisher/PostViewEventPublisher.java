package faang.school.postservice.publisher;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event_broker.PostViewEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final AsyncPostViewEventPublisher asyncPublisher;
    private final UserContext userContext;

    public void publish(Post post) {
        long viewerId = userContext.getUserId();
        PostViewEvent event = new PostViewEvent(post.getId(), viewerId, LocalDateTime.now());
        asyncPublisher.asyncPublish(event);
    }
}
