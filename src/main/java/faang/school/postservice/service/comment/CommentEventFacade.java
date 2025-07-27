package faang.school.postservice.service.comment;

import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventFacade {
    private final CommentPublisher commentPublisher;
    private final CommentEventMapper commentEventMapper;

    // TODO: пулл потоков
    @Async("")
    public void sendCommentMessage(Comment comment) {
        CommentEvent commentEvent = commentEventMapper.toCommentEvent(comment);
        log.info("Mapping Comment entity to CommentEvent. Entity content: {}. Event content: {}.",
                comment, commentEvent);

        commentPublisher.publish(commentEvent);
    }
}
