package faang.school.postservice.aop.aspects;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class CommentEventPublishingAspect {
    private final PostService postService;
    private final RedisCommentEventPublisher commentEventPublisher;

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.PublishEvent)",
            returning = "savedComment"
    )
    public void publishCommentEventAdvice(JoinPoint joinPoint, Object savedComment) {
        if (!(savedComment instanceof Comment)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Long postId = (Long) args[0];
        Comment comment = (Comment) args[1];

        Post post = postService.findPostById(postId);

        if (!post.getAuthorId().equals(comment.getAuthorId())) {
            Comment savedCommentObj = (Comment) savedComment;
            CommentEvent event = new CommentEvent(
                    postId,
                    comment.getAuthorId(),
                    savedCommentObj.getId(),
                    LocalDateTime.now()
            );
            commentEventPublisher.publishCommentEvent(event);
        }
    }
}