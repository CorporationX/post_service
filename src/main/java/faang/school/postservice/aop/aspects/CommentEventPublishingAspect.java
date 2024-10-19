package faang.school.postservice.aop.aspects;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentEventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class CommentEventPublishingAspect {
    private final CommentEventService commentEventService;

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

        commentEventService.handleCommentEvent(postId, (Comment) savedComment);
    }
}