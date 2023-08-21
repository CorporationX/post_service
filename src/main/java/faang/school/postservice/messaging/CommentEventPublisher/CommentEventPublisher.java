package faang.school.postservice.messaging.CommentEventPublisher;

import faang.school.postservice.dto.comment.CommentEventDto;

public interface CommentEventPublisher {
    CommentEventDto publish(CommentEventDto commentEventDto);
}
