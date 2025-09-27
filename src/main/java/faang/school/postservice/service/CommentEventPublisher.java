package faang.school.postservice.service;

import faang.school.postservice.dto.event.CommentEvent;

public interface CommentEventPublisher {
    void publishCommentEvent(CommentEvent event);
}
