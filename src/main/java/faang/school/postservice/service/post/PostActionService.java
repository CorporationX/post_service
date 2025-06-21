package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.PostViewEventDto;
import faang.school.postservice.publisher.post.PostViewEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostActionService {

    private final PostViewEventPublisher redisPostViewEventPublisher;

    public PostActionService(@Qualifier("redisPostViewEventPublisher") PostViewEventPublisher redisPostViewEventPublisher) {
        this.redisPostViewEventPublisher = redisPostViewEventPublisher;
    }

    public void registerPostView(Long postId, Long authorId, Long viewerId) {
        if (viewerId == null || viewerId.equals(authorId)) {
            return;
        }

        PostViewEventDto event = new PostViewEventDto(postId, authorId, viewerId, LocalDateTime.now());
        redisPostViewEventPublisher.publish(event);
    }
}
