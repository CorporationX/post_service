package faang.school.postservice.aop;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.CommentEventProducer;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisherAspect {

    private final CommentEventProducer commentEventProducer;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @AfterReturning(pointcut = "@annotation(PublishCommentEvent)", returning = "result")
    public void publishCommentEvent(JoinPoint joinPoint, Object result) {
        if (!(result instanceof Comment comment)) {
            log.warn("AOP: Method {} returned unsupported type {}",
                    joinPoint.getSignature().getName(),
                    result != null ? result.getClass().getSimpleName() : "null");
            return;
        }

        Post post = comment.getPost();
        if (post == null) {
            post = postRepository.findById(comment.getPost().getId())
                    .orElseThrow(() -> new IllegalStateException("Post not found for comment " + comment.getId()));
        }

        UserDto author = userServiceClient.getUser(comment.getAuthorId());

        if (post.getAuthorId().equals(author.id())) {
            log.info("Skipping self-comment event (postId={}, userId={})", post.getId(), author.id());
            return;
        }

        String postShortContent = getShortContent(post.getContent(), 60);

        CommentEventDto event = CommentEventDto.builder()
                .commentId(comment.getId())
                .postId(post.getId())
                .postContent(postShortContent)
                .commentAuthorId(author.id())
                .commentAuthorName(author.username())
                .postAuthorId(post.getAuthorId())
                .commentText(comment.getContent())
                .build();

        log.info("AOP: Publishing enriched CommentEvent [postId={}, commentId={}, author={}]",
                event.postId(), event.commentId(), event.commentAuthorName());

        commentEventProducer.publish(event);
    }

    private String getShortContent(String content, int limit) {
        if (content == null) return "";
        return content.length() > limit
                ? content.substring(0, limit) + "..."
                : content;
    }
}
