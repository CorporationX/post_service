package faang.school.postservice.service.publisher.comment;

import faang.school.postservice.dto.event.CommentEventDto;

public interface CommentEventPublisher {
    void publish(CommentEventDto event);
}
