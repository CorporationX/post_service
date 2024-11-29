package faang.school.postservice.aop.aspects;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentNotificationEventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Lazy
@Component
public class CommentNotificationEventPublishingAspect {
    private final CommentNotificationEventService commentNotificationEventService;

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.PublishCommentNotificationEvent)",
            returning = "savedComment"
    )
    public void publishCommentNotificationEventAdvice(JoinPoint joinPoint, Object savedComment) {
        if (!(savedComment instanceof Comment)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Long postId = (Long) args[0];

        commentNotificationEventService.handleCommentEvent(postId, (Comment) savedComment);
    }
}
