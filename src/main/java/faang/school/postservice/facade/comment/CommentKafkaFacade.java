package faang.school.postservice.facade.comment;

import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.event.comment.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentKafkaMapper;
import faang.school.postservice.publisher.comment.CommentKafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentKafkaFacade {
    private final CommentKafkaMapper commentKafkaMapper;
    private final CommentKafkaPublisher commentKafkaPublisher;

    @Async("sendKafkaMessage")
    public void createCommentEvent(Comment comment) {
        CommentEventDto commentEventDto = commentKafkaMapper.toCommentEventDto(comment);
        log.debug("Mapping Comment entity to CommentEventDto. Entity content: {}. DTO content: {}.",
                commentEventDto, comment);

        commentKafkaPublisher.sendMessage(commentEventDto);
    }
}