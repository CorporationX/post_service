package faang.school.postservice.util;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventCreator {
    private final CommentEventMapper commentEventMapper;

    public CommentEvent create(Comment comment) {
        return commentEventMapper.toEvent(comment);
    }
}
