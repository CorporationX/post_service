package faang.school.postservice.publisher;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final AsyncCommentEventPublisher asyncPublisher;

    public void publish(CommentEvent event) {
        asyncPublisher.asyncPublish(event);
    }
}
