package faang.school.postservice.consumer.comment;

import faang.school.postservice.aop.comment.CommentCreatedEvent;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.CommentEventProducer;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentCreatedEventListener {

    private final CommentEventProducer commentEventProducer;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CommentCreatedEvent event) {

        Comment comment = event.getComment();

        UserDto author = userServiceClient.getUser(comment.getAuthorId());
        Post post = postRepository.getByIdOrThrow(comment.getPost().getId());

        if (post.getAuthorId().equals(author.id())) {
            log.debug(
                    "Skip self-comment event [postId={}, userId={}]",
                    post.getId(),
                    author.id()
            );
            return;
        }

        CommentEventDto dto =
                CommentMapper.toDto(comment, author, post);

        commentEventProducer.publish(dto);

        log.info(
                "Comment event published [commentId={}, postId={}]",
                dto.commentId(),
                dto.postId()
        );
    }
}
