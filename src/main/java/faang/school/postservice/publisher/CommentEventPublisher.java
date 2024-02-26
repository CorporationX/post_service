package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.CommentEvent;
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
