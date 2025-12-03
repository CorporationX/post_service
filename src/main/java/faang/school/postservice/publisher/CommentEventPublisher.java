package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;

public interface CommentEventPublisher {
    void publish(CommentEvent event);
}
