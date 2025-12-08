package faang.school.postservice.producer;

import faang.school.postservice.dto.event.CommentEvent;

public interface CommentEventPublisher {
    void publish(CommentEvent event);
}
