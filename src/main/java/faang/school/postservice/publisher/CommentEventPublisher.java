package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;

public interface CommentEventPublisher {
    void publish(CommentEvent event);
}
