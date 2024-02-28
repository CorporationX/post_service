package faang.school.postservice.publisher;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event_broker.PostViewEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher extends AsyncEventPublisher<PostViewEvent>{
    private final UserContext userContext;

    @Value("${spring.kafka.topics.post_view.name}")
    private String postViewTopic;

    @Override
    protected String getTopicName() {
        return null;
    }

    public void publish(Post post) {
        long viewerId = userContext.getUserId();
        PostViewEvent event = new PostViewEvent(post.getId(), viewerId, LocalDateTime.now());
        asyncPublish(event);
    }
}
