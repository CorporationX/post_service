package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.CommentKafkaProducer;
import faang.school.postservice.kafka.producer.AuthorCommentKafkaProducer;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.model.event.kafka.AuthorCommentKafkaEvent;
import faang.school.postservice.redis.publisher.CommentEventPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommitedEventListener {
    private final CommentEventPublisher commentEventPublisher;
    private final PostRepository postRepository;
    private final CommentKafkaProducer commentKafkaProducer;
    private final AuthorCommentKafkaProducer authorCommentKafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCommittedEvent(CommentCommittedEvent event) {
        CommentDto savedCommentDto = event.getCommentDto();
        commentEventPublisher.publish(createCommentEvent(savedCommentDto));

        authorCommentKafkaProducer.sendEvent(new AuthorCommentKafkaEvent(savedCommentDto.getAuthorId()));

        CommentSentKafkaEvent commentSentKafkaEvent = new CommentSentKafkaEvent(
                savedCommentDto.getPostId(),
                savedCommentDto.getAuthorId(),
                savedCommentDto.getId(),
                savedCommentDto.getContent());
        commentKafkaProducer.sendEvent(commentSentKafkaEvent);
    }

    private CommentEvent createCommentEvent(CommentDto comment) {
        Long postId = comment.getPostId();
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        Post post = optionalPost.get();
        Long postAuthorId = post.getAuthorId();
        Long authorId = comment.getAuthorId();
        String postText = comment.getContent();
        Long commentId = comment.getId();
        return new CommentEvent(authorId, postAuthorId, postId, postText, commentId);
    }
}
